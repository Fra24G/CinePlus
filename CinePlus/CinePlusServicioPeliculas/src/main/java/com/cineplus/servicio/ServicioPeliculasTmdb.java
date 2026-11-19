package com.cineplus.servicio;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.PeliculaExternaDTO;
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

    private static final String PREFIJO_ARCHIVO = "archivo:";
    private static final long MAXIMO_BYTES = 5_000_000;

    
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

   
@Override
public PeliculaExternaDTO obtenerDetalle(String idExterno) throws Exception {

    String clave = leerClave();

    if (clave == null || idExterno == null
            || !idExterno.trim().matches("[0-9]+")) {
        throw new Exception("No se pudo obtener la película de TMDB.");
    }

    try {
        String ruta = "movie/" + idExterno.trim()
                + "?language=es-MX&append_to_response=credits,release_dates";

        HttpResponse<String> respuesta = clienteHttp.send(
                crearSolicitud(clave, ruta),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (respuesta.statusCode() != 200) {
            throw new IOException("TMDB no respondió correctamente.");
        }

        JsonObject datos = JsonParser.parseString(respuesta.body())
                .getAsJsonObject();

        String id = datos.has("id")
                ? datos.get("id").getAsString() : idExterno;

        String titulo = leerTexto(datos, "title");
        String director = leerDirector(datos);
        String genero = leerGeneroDetalle(datos);
        int duracion = leerEntero(datos, "runtime");
        LocalDate estreno = leerFecha(leerTexto(datos, "release_date"));
        String clasificacion = leerClasificacion(datos);
        String sinopsis = leerTexto(datos, "overview");
        String imagen = leerTexto(datos, "poster_path");

        if (titulo.isBlank()) {
            throw new IOException("No se obtuvo el título.");
        }

        return new PeliculaExternaDTO(
                id,
                titulo,
                director,
                genero,
                duracion,
                estreno,
                clasificacion,
                sinopsis,
                imagen
        );

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new Exception("No se pudo obtener la película de TMDB.", e);

    } catch (IOException | RuntimeException e) {
        throw new Exception("No se pudo obtener la película de TMDB.", e);
    }
}

    
@Override
public List<PeliculaDTO> buscar(String texto) throws Exception {

    if (texto == null || texto.trim().length() < 2) {
        return List.of();
    }

    String clave = leerClave();

    // Sin clave, busca dentro del catálogo de muestra.
    if (clave == null) {
        String buscado = texto.trim().toLowerCase(Locale.ROOT);
        List<PeliculaDTO> resultado = new ArrayList<>();

        for (PeliculaDTO pelicula : muestra("Selección de muestra").peliculas()) {

            if (pelicula.titulo() != null
                    && pelicula.titulo().toLowerCase(Locale.ROOT).contains(buscado)) {
                resultado.add(pelicula);
            }

            if (resultado.size() == MAXIMO_PELICULAS) {
                break;
            }
        }

        return resultado;
    }

    try {
        String consulta = URLEncoder.encode(
                texto.trim(), StandardCharsets.UTF_8);

        String ruta = "search/movie?query=" + consulta
                + "&language=es-MX";

        HttpResponse<String> respuesta = clienteHttp.send(
                crearSolicitud(clave, ruta),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (respuesta.statusCode() != 200) {
            throw new IOException("TMDB no respondió correctamente.");
        }

        return parsear(respuesta.body());

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new Exception("No se pudo buscar películas en TMDB.", e);

    } catch (IOException | RuntimeException e) {
        throw new Exception("No se pudo buscar películas en TMDB.", e);
    }
}


    
@Override
public String guardarPosterLocal(byte[] imagen, String nombreArchivo)
        throws Exception {

    if (imagen == null || imagen.length == 0) {
        throw new Exception("Debes seleccionar una imagen.");
    }

    if (imagen.length >= MAXIMO_BYTES) {
        throw new Exception("El poster debe pesar menos de 5 MB.");
    }

    String extension;

    try (ImageInputStream entrada = ImageIO.createImageInputStream(
            new ByteArrayInputStream(imagen))) {

        if (entrada == null) {
            throw new Exception("El archivo no es una imagen valida.");
        }

        Iterator<ImageReader> lectores = ImageIO.getImageReaders(entrada);

        if (!lectores.hasNext()) {
            throw new Exception("El archivo no es una imagen valida.");
        }

        ImageReader lector = lectores.next();

        try {
            lector.setInput(entrada, true, true);

            if (lector.read(0) == null) {
                throw new Exception("El archivo no es una imagen valida.");
            }

            extension = extensionImagen(lector.getFormatName());

        } finally {
            lector.dispose();
        }

    } catch (IOException | RuntimeException e) {
        throw new Exception("No se pudo leer la imagen seleccionada.", e);
    }

    try {
        Path carpeta = Path.of(
                System.getProperty("user.home"),
                ".cineplus",
                "posters"
        );

        Files.createDirectories(carpeta);

        String base = nombreBase(nombreArchivo);

        String nombreUnico = base + "-"
                + UUID.randomUUID() + "." + extension;

        Files.write(carpeta.resolve(nombreUnico), imagen);

        return PREFIJO_ARCHIVO + nombreUnico;

    } catch (IOException | RuntimeException e) {
        throw new Exception("No se pudo guardar el póster.", e);
    }
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

    return (clave == null || clave.isBlank())
            ? null : clave.trim();
}


    /**
     * Un token largo (empieza con "eyJ") va en el encabezado; una clave corta,
     * en la direccion.
     */
   
private HttpRequest crearSolicitud(String clave) {
    return crearSolicitud(
            clave,
            "movie/now_playing?language=es-MX&region=MX&page=1"
    );
}

private HttpRequest crearSolicitud(String clave, String ruta) {

    boolean esToken = clave.startsWith("eyJ");
    String rutaCompleta = ruta;

    if (!esToken) {
        String separador = rutaCompleta.contains("?") ? "&" : "?";

        rutaCompleta += separador + "api_key="
                + URLEncoder.encode(clave, StandardCharsets.UTF_8);
    }

    HttpRequest.Builder solicitud = HttpRequest.newBuilder(
            URL_BASE.resolve(rutaCompleta))
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
        // Pelicula de TMDB: todavia no esta en la BD, por eso idPelicula va en null
        // y el id de TMDB viaja en idExterno.
        // Los datos que TMDB no da en la lista (director, duracion, clasificacion) van vacios
        return new PeliculaDTO(null, String.valueOf(id), titulo, "", genero, 0, estreno, "",
                sinopsis, imagen, true, null, null);
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
    if (texto == null || texto.isBlank()) {
        return null;
    }

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

private static int leerEntero(JsonObject datos, String campo) {

    JsonElement valor = datos.get(campo);

    if (valor == null || valor.isJsonNull()) {
        return 0;
    }

    try {
        return valor.getAsInt();
    } catch (RuntimeException e) {
        return 0;
    }
}

private static String leerDirector(JsonObject datos) {

    if (!datos.has("credits")
            || !datos.get("credits").isJsonObject()) {
        return "";
    }

    JsonObject creditos = datos.getAsJsonObject("credits");

    if (!creditos.has("crew")
            || !creditos.get("crew").isJsonArray()) {
        return "";
    }

    for (JsonElement elemento : creditos.getAsJsonArray("crew")) {

        JsonObject persona = elemento.getAsJsonObject();

        if ("Director".equalsIgnoreCase(
                leerTexto(persona, "job"))) {
            return leerTexto(persona, "name");
        }
    }

    return "";
}

private static String leerGeneroDetalle(JsonObject datos) {

    if (!datos.has("genres")
            || !datos.get("genres").isJsonArray()) {
        return "Película";
    }

    JsonArray generos = datos.getAsJsonArray("genres");

    if (generos.isEmpty()) {
        return "Película";
    }

    return leerTexto(
            generos.get(0).getAsJsonObject(), "name");
}

private static String leerClasificacion(JsonObject datos) {

    if (datos.has("release_dates")
            && datos.get("release_dates").isJsonObject()) {

        JsonArray paises = datos.getAsJsonObject("release_dates")
                .getAsJsonArray("results");

        if (paises != null) {

            String mexico = buscarCertificacion(paises, "MX");
            String resultado = convertirClasificacion(mexico, true);

            if (!resultado.isBlank()) {
                return resultado;
            }

            String estadosUnidos = buscarCertificacion(paises, "US");
            resultado = convertirClasificacion(estadosUnidos, false);

            if (!resultado.isBlank()) {
                return resultado;
            }
        }
    }

    // Valor provisional si TMDB no publica una clasificación.
    return "PG";
}

private static String buscarCertificacion(
        JsonArray paises, String codigoPais) {

    for (JsonElement elemento : paises) {

        JsonObject pais = elemento.getAsJsonObject();

        if (!codigoPais.equalsIgnoreCase(
                leerTexto(pais, "iso_3166_1"))) {
            continue;
        }

        JsonArray fechas = pais.getAsJsonArray("release_dates");

        if (fechas == null) {
            continue;
        }

        for (JsonElement fecha : fechas) {

            String certificacion = leerTexto(
                    fecha.getAsJsonObject(), "certification");

            if (!certificacion.isBlank()) {
                return certificacion;
            }
        }
    }

    return "";
}

private static String convertirClasificacion(
        String certificacion, boolean esMexico) {

    if (certificacion == null || certificacion.isBlank()) {
        return "";
    }

    String valor = certificacion.trim()
            .toUpperCase(Locale.ROOT)
            .replace(" ", "")
            .replace("-", "");

    if (!esMexico) {
        return switch (valor) {
            case "G" -> "G";
            case "PG" -> "PG";
            case "PG13" -> "PG-13";
            case "R" -> "R";
            case "NC17" -> "NC-17";
            default -> "";
        };
    }

    return switch (valor) {
        case "AA", "A" -> "G";
        case "B" -> "PG";
        case "B15" -> "PG-13";
        case "C" -> "R";
        case "D" -> "NC-17";
        default -> "";
    };
}

private static String extensionImagen(String formato)
        throws Exception {

    String valor = formato.toLowerCase(Locale.ROOT);

    return switch (valor) {
        case "jpeg", "jpg" -> "jpg";
        case "png" -> "png";
        case "gif" -> "gif";
        case "bmp" -> "bmp";
        case "wbmp" -> "wbmp";
        default -> throw new Exception(
                "El formato de imagen no es compatible.");
    };
}

private static String nombreBase(String nombreArchivo) {

    if (nombreArchivo == null || nombreArchivo.isBlank()) {
        return "poster";
    }

    String nombre = nombreArchivo.replace('\\', '/');

    nombre = nombre.substring(nombre.lastIndexOf('/') + 1);

    int punto = nombre.lastIndexOf('.');

    if (punto > 0) {
        nombre = nombre.substring(0, punto);
    }

    nombre = nombre.replaceAll("[^a-zA-Z0-9_-]", "_");

    if (nombre.isBlank()) {
        return "poster";
    }

    return nombre.length() > 35
            ? nombre.substring(0, 35) : nombre;
}

}
