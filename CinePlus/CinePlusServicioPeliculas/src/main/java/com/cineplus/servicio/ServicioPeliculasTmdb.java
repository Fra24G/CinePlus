package com.cineplus.servicio;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PATRON ADAPTER. Adapta la API de TMDB a nuestra interfaz IServicioPeliculas y
 * a nuestros DTOs. La clave de TMDB no va en el codigo: se lee de la propiedad
 * de sistema cineplus.tmdb.token o de la variable de entorno
 * CINEPLUS_TMDB_TOKEN. Si no hay clave o TMDB falla, usa el catalogo de
 * respaldo (muestra.json).
 *
 * En el campo imagen del PeliculaDTO se guarda la ruta del poster en TMDB (por
 * ejemplo "/abc.jpg") o, para el catalogo de respaldo, "recurso:nombre.jpg".
 *
 */
public class ServicioPeliculasTmdb implements IServicioPeliculas {

    /**
     * Prefijo que marca un poster incluido dentro del programa.
     */
    public static final String PREFIJO_RECURSO = "recurso:";
    /**
     * Clave de respaldo para que funcione sin configurar nada.
     */
    private static final String CLAVE_PREDETERMINADA = "2886c538acb98bef8931539c13adb704";
    private static final URI URL_BASE = URI.create("https://api.themoviedb.org/3/");
    private static final int MAXIMO_PELICULAS = 12;

    // Nombres de los generos de TMDB (solo se muestra el primero de cada pelicula)
    private static final Map<Integer, String> GENEROS = Map.ofEntries(
            Map.entry(28, "Acción"), Map.entry(12, "Aventura"), Map.entry(16, "Animación"),
            Map.entry(35, "Comedia"), Map.entry(80, "Crimen"), Map.entry(99, "Documental"),
            Map.entry(18, "Drama"), Map.entry(10751, "Familiar"), Map.entry(14, "Fantasía"),
            Map.entry(36, "Historia"), Map.entry(27, "Terror"), Map.entry(10402, "Música"),
            Map.entry(9648, "Misterio"), Map.entry(10749, "Romance"),
            Map.entry(878, "Ciencia ficción"), Map.entry(53, "Suspenso"),
            Map.entry(10752, "Bélica"), Map.entry(37, "Western"));

    private final HttpClient clienteHttp
            = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();
    private final RepositorioPosters posters = new RepositorioPosters();

    @Override
    public CarteleraDTO consultarCartelera() {
        String clave = leerClave();
        if (clave == null) {
            return muestra("Selección de muestra · falta configurar la clave de TMDB");
        }
        try {
            HttpResponse<String> respuesta
                    = clienteHttp.send(crearSolicitud(clave), HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() != 200) {
                return muestra(respuesta.statusCode() == 401
                        ? "Selección de muestra · TMDB rechazó la clave"
                        : "Selección de muestra · TMDB no disponible");
            }
            List<PeliculaDTO> peliculas = parsear(respuesta.body());
            return new CarteleraDTO(peliculas, true, peliculas.isEmpty()
                    ? "No hay películas disponibles por el momento."
                    : "En cines");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return muestra("Selección de muestra");
        } catch (IOException | RuntimeException e) {
            return muestra("Selección de muestra · sin conexión a TMDB");
        }
    }

    @Override
    public byte[] obtenerPoster(PeliculaDTO pelicula) {
        return posters.obtener(pelicula);
    }

    // ---------- consulta a TMDB ----------
    /**
     * Lee la clave de la propiedad de sistema o de la variable de entorno.
     */
    private static String leerClave() {
        String clave = System.getProperty("cineplus.tmdb.token");
        if (clave == null || clave.isBlank()) {
            clave = System.getenv("CINEPLUS_TMDB_TOKEN");
        }
        if (clave == null || clave.isBlank()) {
            clave = CLAVE_PREDETERMINADA;
        }
        return clave.trim();
    }

    /**
     * Un token largo (empieza con "eyJ") va en el encabezado; una clave corta,
     * en la direccion.
     */
    private HttpRequest crearSolicitud(String clave) {
        boolean esToken = clave.startsWith("eyJ");
        String ruta = "movie/now_playing?language=es-MX&region=MX&page=1";
        if (!esToken) {
            ruta += "&api_key=" + URLEncoder.encode(clave, StandardCharsets.UTF_8);
        }
        HttpRequest.Builder solicitud = HttpRequest.newBuilder(URL_BASE.resolve(ruta))
                .timeout(Duration.ofSeconds(7))
                .header("Accept", "application/json");
        if (esToken) {
            solicitud.header("Authorization", "Bearer " + clave);
        }
        return solicitud.GET().build();
    }

    /**
     * Convierte la respuesta JSON de TMDB en una lista de PeliculaDTO.
     */
    public static List<PeliculaDTO> parsear(String json) {
        JsonArray resultados = JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("results");
        if (resultados == null) {
            throw new JsonParseException("Falta el arreglo results");
        }
        List<PeliculaDTO> peliculas = new ArrayList<>();
        for (JsonElement elemento : resultados) {
            JsonObject datos = elemento.getAsJsonObject();
            if (!datos.has("id") || !datos.has("title")) {
                continue;
            }
            peliculas.add(crearPelicula(
                    datos.get("id").getAsInt(),
                    leerTexto(datos, "title"),
                    primerGenero(datos),
                    leerFecha(leerTexto(datos, "release_date")),
                    leerTexto(datos, "overview"),
                    leerTexto(datos, "poster_path")));
            if (peliculas.size() == MAXIMO_PELICULAS) {
                break;
            }
        }
        return peliculas;
    }

    // ---------- catalogo de respaldo ----------
    /**
     * Carga las peliculas de muestra.json (incluido dentro del programa).
     */
    private CarteleraDTO muestra(String estado) {
        try (InputStream entrada = getClass().getResourceAsStream("/cineplus/catalogo/muestra.json")) {
            if (entrada == null) {
                return new CarteleraDTO(List.of(), false, estado);
            }
            JsonArray resultados = JsonParser
                    .parseReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))
                    .getAsJsonArray();
            List<PeliculaDTO> peliculas = new ArrayList<>();
            for (JsonElement elemento : resultados) {
                JsonObject datos = elemento.getAsJsonObject();
                peliculas.add(crearPelicula(
                        datos.get("id").getAsInt(),
                        leerTexto(datos, "titulo"),
                        leerTexto(datos, "genero"),
                        leerFecha(leerTexto(datos, "estreno")),
                        "",
                        PREFIJO_RECURSO + leerTexto(datos, "recursoLocal")));
            }
            return new CarteleraDTO(peliculas, false, estado);
        } catch (IOException | RuntimeException e) {
            return new CarteleraDTO(List.of(), false, "No se pudo cargar la selección de muestra.");
        }
    }

    // ---------- utilidades ----------
    private static PeliculaDTO crearPelicula(int id, String titulo, String genero,
            LocalDate estreno, String sinopsis, String imagen) {
        // Los datos que TMDB no da en la lista (director, duracion, clasificacion) van vacios
        return new PeliculaDTO(id, titulo, "", genero, 0, estreno, "", sinopsis, imagen,
                true, null, null);
    }

    private static String leerTexto(JsonObject datos, String campo) {
        JsonElement valor = datos.get(campo);
        return (valor == null || valor.isJsonNull()) ? "" : valor.getAsString();
    }

    /**
     * Acepta "2014-11-05" o solo el anio "2014". Si no se entiende, devuelve
     * null.
     */
    private static LocalDate leerFecha(String texto) {
        try {
            if (texto.length() == 4) {
                return LocalDate.of(Integer.parseInt(texto), 1, 1);
            }
            return LocalDate.parse(texto);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static String primerGenero(JsonObject datos) {
        JsonElement ids = datos.get("genre_ids");
        if (ids != null && ids.isJsonArray() && !ids.getAsJsonArray().isEmpty()) {
            return GENEROS.getOrDefault(ids.getAsJsonArray().get(0).getAsInt(), "Película");
        }
        return "Película";
    }
}
