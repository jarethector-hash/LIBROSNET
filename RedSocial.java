package Proyecto_Final;

import java.util.*;

public class RedSocial {
    private final List<Usuario> usuarios;
    private final List<Publicacion> publicaciones;
    private final GrafoAmigos grafo;
    private final Map<Integer, ColaNotificaciones> notifications;
    private final PilaAcciones pilaAcciones;
    private final ListaDoblePublicaciones listaDoble;

    public RedSocial() {
        usuarios = new ArrayList<>();
        publicaciones = new ArrayList<>();
        grafo = new GrafoAmigos();
        notifications = new HashMap<>();
        pilaAcciones = new PilaAcciones();
        listaDoble = new ListaDoblePublicaciones();
    }

    public Usuario crearUsuario(String nombre, String bio) {
        Usuario u = new Usuario(nombre, bio);
        usuarios.add(u);
        grafo.agregarUsuario(u.getId());
        notifications.putIfAbsent(u.getId(), new ColaNotificaciones());
        return u;
    }

    public Publicacion crearPublicacion(int autorId, String texto, String imagenPath) {
        Publicacion p = new Publicacion(autorId, texto, imagenPath);
        publicaciones.add(p);
        listaDoble.insertarFrente(p);
        // Push action
        pilaAcciones.push(new Accion(Accion.Tipo.PUBLICAR, p));
        return p;
    }

    public List<Usuario> getAllUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Publicacion> getAllPublicaciones() {
        return new ArrayList<>(publicaciones);
    }

    public Usuario getUsuarioById(int id) {
        for (Usuario u : usuarios) if (u.getId() == id) return u;
        return null;
    }

    public Publicacion getPublicacionById(int id) {
        for (Publicacion p : publicaciones) if (p.getId() == id) return p;
        return null;
    }

    public List<Usuario> buscarUsuariosPorNombre(String texto) {
        List<Usuario> res = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) return res;
        String lower = texto.toLowerCase(Locale.ROOT);
        for (Usuario u : usuarios) {
            if (u.getNombre() != null && u.getNombre().toLowerCase(Locale.ROOT).contains(lower)) res.add(u);
        }
        return res;
    }

    public List<Publicacion> buscarPostsPorKeyword(String keyword) {
        List<Publicacion> res = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) return res;
        String lower = keyword.toLowerCase(Locale.ROOT);
        for (Publicacion p : publicaciones) {
            if (p.getTexto() != null && p.getTexto().toLowerCase(Locale.ROOT).contains(lower)) res.add(p);
        }
        return res;
    }

    public void reaccionar(int postId, String tipo) {
        Publicacion p = getPublicacionById(postId);
        if (p != null) p.reaccionar(tipo);
    }

    public void comentar(int postId, String comentario) {
        Publicacion p = getPublicacionById(postId);
        if (p != null) p.comentar(comentario);
    }

    public void agregarAmistad(int id1, int id2) {
        grafo.conectar(id1, id2);
        Usuario u1 = getUsuarioById(id1);
        Usuario u2 = getUsuarioById(id2);
        if (u1 != null) u1.agregarAmigo(id2);
        if (u2 != null) u2.agregarAmigo(id1);
        notifications.putIfAbsent(id1, new ColaNotificaciones());
        notifications.putIfAbsent(id2, new ColaNotificaciones());
        notifications.get(id1).push(new Notificacion("Ahora eres amigo de " + (u2 != null ? u2.getNombre() : id2)));
        notifications.get(id2).push(new Notificacion("Ahora eres amigo de " + (u1 != null ? u1.getNombre() : id1)));
    }

    public List<Notificacion> getNotificacionesUsuario(int id) {
        return notifications.containsKey(id) ? notifications.get(id).toList() : new ArrayList<>();
    }

    public List<Usuario> sugerirAmigos(int id) {
        Set<Integer> sugeridos = grafo.sugerirAmigos(id);
        List<Usuario> res = new ArrayList<>();
        for (Integer sid : sugeridos) {
            Usuario su = getUsuarioById(sid);
            if (su != null) res.add(su);
        }
        return res;
    }

    // Additional helper: remove publication
    public boolean eliminarPublicacion(int id) {
        Publicacion p = getPublicacionById(id);
        if (p == null) return false;
        publicaciones.remove(p);
        listaDoble.eliminarPorId(id);
        pilaAcciones.push(new Accion(Accion.Tipo.ELIMINAR_PUBLICACION, id));
        return true;
    }
}
