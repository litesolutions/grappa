package com.github.fge.grappa.transform.load;

import com.github.fge.grappa.transform.ParserTransformException;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loader sin reflexión al JDK: usa MethodHandles.Lookup#defineClass
 * Necesita una clase "anchor" (normalmente, la clase del parser base)
 * en el mismo paquete de la clase generada.
 */
public final class LookupClassLoader implements AutoCloseable {

    private final ClassLoader loader;
    private final Class<?> anchor; // p.ej., la clase del parser base
    private final Map<String, Class<?>> cache = new ConcurrentHashMap<>();

    /**
     * @param loader classloader objetivo (normalmente, el del parser)
     * @param anchor clase ya cargada en el paquete destino (parser base)
     */
    public LookupClassLoader(final ClassLoader loader, final Class<?> anchor) {
        this.loader = Objects.requireNonNull(loader, "loader");
        this.anchor = Objects.requireNonNull(anchor, "anchor");
    }

    /**
     * Devuelve la clase si ya está cargada/disponible por nombre.
     * No usa findLoadedClass; delega en Class.forName sin inicializar.
     */
    @Nullable
    public Class<?> findClass(final String className) {
        Objects.requireNonNull(className, "className");

        // cache rápido propio para evitar re-definiciones
        Class<?> cached = cache.get(className);
        if (cached != null) return cached;

        try {
            Class<?> cls = Class.forName(className, /* initialize */ false, loader);
            cache.put(className, cls);
            return cls;
        } catch (ClassNotFoundException e) {
            return null; // aún no está definida/cargada
        }
    }

    /**
     * Define la clase generada en el mismo paquete/loader que 'anchor'
     * usando MethodHandles.privateLookupIn(...).defineClass(byte[]).
     */
    public Class<?> loadClass(final String className, final byte[] bytecode) {
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(bytecode, "bytecode");

        // si ya la tenemos, devuélvela
        Class<?> already = findClass(className);
        if (already != null) return already;

        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandles.Lookup privateLookup =
                    MethodHandles.privateLookupIn(anchor, lookup);

            Class<?> defined = privateLookup.defineClass(bytecode);
            // sanity check opcional: mismo nombre esperado
            if (!defined.getName().equals(className)) {
               throw new ParserTransformException(
                    "Nombre inesperado. Esperado=" + className + " Definido=" + defined.getName()
                );
            }
            cache.put(defined.getName(), defined);
            return defined;
        } catch (IllegalAccessException e) {
            throw new ParserTransformException(
                "No se pudo obtener privateLookupIn sobre el anchor " + anchor.getName() +
                " (¿mismos paquete/loader?)", e);
        } catch (Throwable t) {
            throw new ParserTransformException("No se pudo definir la clase generada: " + className, t);
        }
    }

    @Override
    public void close() {
        cache.clear();
    }
}
