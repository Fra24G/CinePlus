package com.cineplus.servicio;

import com.cineplus.dto.PeliculaDTO;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Obtiene los bytes del poster de una pelicula.
 * Orden: poster incluido en el programa (catalogo de respaldo), copia guardada en
 * ~/.cineplus/cache, y por ultimo descarga desde el CDN de TMDB (que se guarda en la cache).
 * Si algo falla devuelve un arreglo vacio, y la pantalla muestra "poster no disponible".
 *
 */
public class RepositorioPosters {

    private static final long MAXIMO_BYTES = 5_000_000;
    private static final String URL_IMAGENES = "https://image.tmdb.org/t/p/w342";
    private static final String PREFIJO_ARCHIVO = "archivo:";

    private final HttpClient clienteHttp =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4)).build();

    public byte[] obtener(PeliculaDTO pelicula) {

    if (pelicula == null) {
        return new byte[0];
    }

    String imagen = pelicula.imagen();

    if (imagen == null || imagen.isBlank()) {
        return new byte[0];
    }

    // Poster incluido dentro del programa.
    if (imagen.startsWith(ServicioPeliculasTmdb.PREFIJO_RECURSO)) {
        return leerRecurso(
                imagen.substring(ServicioPeliculasTmdb.PREFIJO_RECURSO.length())
        );
    }

    // Poster guardado por el administrador.
    if (imagen.startsWith(PREFIJO_ARCHIVO)) {
        return leerArchivoLocal(
                imagen.substring(PREFIJO_ARCHIVO.length())
        );
    }

    // Poster descargado desde TMDB.
    return descargar(imagen);
}

    /** Lee un poster incluido dentro del programa (carpeta resources) */
    private byte[] leerRecurso(String nombre) {
        // Solo nombres simples como "coco.jpg": evita rutas raras
        if (!nombre.matches("[a-z0-9-]+\\.jpg")) {
            return new byte[0];
        }
        try (InputStream entrada =
                getClass().getResourceAsStream("/cineplus/catalogo/posters/" + nombre)) {
            return entrada == null ? new byte[0] : entrada.readAllBytes();
        } catch (IOException e) {
            return new byte[0];
        }
    }
    /** Lee un poster guardado por el administrador*/
private byte[] leerArchivoLocal(String nombre) {

    if (nombre == null || !nombre.matches(
            "[a-zA-Z0-9_-]{1,35}-[0-9a-fA-F-]{36}\\.(jpg|png|gif|bmp|wbmp)")) {
        return new byte[0];
    }

    try {
        Path carpeta = Path.of(
                System.getProperty("user.home"),
                ".cineplus",
                "posters"
        ).toAbsolutePath().normalize();

        Path archivo = carpeta.resolve(nombre).normalize();

        // Comprueba que el archivo este dentro de la carpeta permitida
        if (!archivo.startsWith(carpeta)
                || !Files.isRegularFile(archivo)) {
            return new byte[0];
        }

        long tamanio = Files.size(archivo);

        if (tamanio <= 0 || tamanio >= MAXIMO_BYTES) {
            return new byte[0];
        }

        return Files.readAllBytes(archivo);

    } catch (IOException | RuntimeException e) {
        return new byte[0];
    }
}

    /** Busca el poster en la cache y, si no esta, lo descarga de TMDB. */
    private byte[] descargar(String ruta) {
        // Solo rutas como "/abc123.jpg"
        if (!ruta.matches("/[a-zA-Z0-9._-]+\\.(jpg|png)")) {
            return new byte[0];
        }
        Path cache = Path.of(System.getProperty("user.home"), ".cineplus", "cache", ruta.substring(1));
        try {
            if (Files.isRegularFile(cache) && Files.size(cache) < MAXIMO_BYTES) {
                return Files.readAllBytes(cache);
            }
            HttpRequest solicitud = HttpRequest.newBuilder(URI.create(URL_IMAGENES + ruta))
                    .timeout(Duration.ofSeconds(7))
                    .GET()
                    .build();
            HttpResponse<InputStream> respuesta =
                    clienteHttp.send(solicitud, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream entrada = respuesta.body()) {
                if (respuesta.statusCode() != 200) {
                    return new byte[0];
                }
                byte[] datos = entrada.readNBytes((int) MAXIMO_BYTES + 1);
                if (datos.length > MAXIMO_BYTES) {
                    return new byte[0];
                }
                guardarEnCache(cache, datos);
                return datos;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new byte[0];
        } catch (IOException | RuntimeException e) {
            return new byte[0];
        }
    }

    private void guardarEnCache(Path cache, byte[] datos) {
        try {
            Files.createDirectories(cache.getParent());
            Files.write(cache, datos);
        } catch (IOException e) {
            // Guardar en cache es opcional: si falla, el poster igual se muestra
        }
    }
}