package controlador;

import bbdd.ProductosBD;
import modelo.Usuario;
import bbdd.UsuariosBD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import modelo.Producto;
import vista.*;


public class ControladorUsuario {
    public String AnadirUsuarios(Usuario usuario){
        UsuariosBD usuarios = new UsuariosBD();
        String existe = usuarios.existeUsuario(usuario);
        String telefonoString = String.valueOf(usuario.getTelefono());

        if(existe != null){
            return "El usuario escrito ya existe, escriba otro";
        }else if(usuario.getUsuario().equals("") || usuario.getContrasena().equals("") || usuario.getNombre().equals("") || telefonoString.equals("") || usuario.getApellidos().equals("") || usuario.getNacimiento().equals("") || usuario.getGmail().equals("")|| usuario.getPais().equals("")){
            return "Alguno de los campos obigatorios estan vacios";
        } else if(usuario.getUsuario().length() < 4) {
            return "El nombre del usuario es demasiado corto";
        } else if(usuario.getContrasena().length() < 6) {
            return "La contraseña es demasiado corta";
        }  else if (!usuario.getContrasena().matches(".*[a-zA-Z].*")) {
            return "Debes introducir como mínimo algún caracter en la contraseña";
        } else if (!usuario.getContrasena().matches(".*\\d.*")) {
            return "Debes introducir como mínimo algún digito en la contraseña";
        } else if(telefonoString.length() < 9) {
            return "Has introducido de manera incorrecta el número de teléfono";
        } else if(!usuario.getGmail().contains("@")) {
            return "El formato de gmail es incorrecto";
        } else if (!usuario.getNacimiento().matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "El formato de nacimiento es incorrecto (*año-mes-dia*)";
        } else{
            usuarios.anadirUsuariosBD(usuario);
            new Menu();
            return "hecho";
        }

    }  
    
    public String IniciarSesion(Usuario usuario) {
        String rol = new UsuariosBD().consultarInicioSesion(usuario);
       
        return rol;
    }
    
    public Usuario recogerDatosUsuario(Usuario usuario){
        //Recoger los datos en un String
        String[] datos = new UsuariosBD().recogerDatos(usuario);
        
        //Desglosar Datos
        String usuarioNombre = datos[0];
        String contrasena = datos[1];
        String nombre = datos[2];
        String apellidos = datos[3];
        String nacimiento = datos[4];
        int telefono = Integer.parseInt(datos[5]);
        String gmail = datos[6];
        String pais = datos[7];
        String provincia = datos[8];
        String ciudad = datos[9];
        String rol = datos[10];
        float fondos = Float.parseFloat(datos[11]);

        //Crear Usuario
        Usuario usuario1 = new Usuario(usuarioNombre, contrasena, nombre, apellidos, nacimiento, telefono, gmail, pais, provincia, ciudad, rol, fondos);
        
        //Devolver Usuario
        return usuario1;
    }
    
    public String recogerFoto(Usuario usuario){
        String[] datos = new UsuariosBD().recogerURL(usuario);
        String url = datos[12];
        if(url == null){
            return "https://us.123rf.com/450wm/thesomeday123/thesomeday1231712/thesomeday123171200009/91087331-icono-de-perfil-de-avatar-predeterminado-para-hombre-marcador-de-posici%C3%B3n-de-foto-gris-vector-de.jpg?ver=6";
        }else{
            return url;
        }
    }
    
    public String establecerFoto(Usuario usuario, String url){
        if(url.equals("")){
            return "No has introducido ninguna url, por favor, introduzca una.";
        } else if(url.length() < 44 ){
            return "La url introducida es demasiado corta";
        }else{
            new UsuariosBD().establecerFoto(usuario, url);
            return "hecho";
        }
    }
    
    public String anadirFondos(Usuario usuario, float fondos){
        Usuario usuario1 = recogerDatosUsuario(usuario);
        if(usuario1.getFondos() >= 900){
            return "Su cartera ya ha alcanzado el limite";
        }else{
            new UsuariosBD().añadirFondos(usuario, fondos);
            return "Se han añadido correctamente los fondos indicados, muchas gracias.";
        }
    }
    
    public String realizarCompra(Usuario usuario, Producto producto){
        Usuario usuario1 = recogerDatosUsuario(usuario);
        int edad = devolverEdad(usuario1);
        
        if(usuario1.getFondos() <= producto.getPrecio()){
            return "No tienes suficientes fondos como para comprar dicho producto.";
        }else if(edad < producto.getCategoria()){
            return "La categoria del producto no es adecuada para el usuario, pruebe con otro juego.";
        }else{
            usuario1.setFondos(usuario1.getFondos() - producto.getPrecio());
            producto.setUnidades(producto.getUnidades()-1);
            System.out.println(usuario1.getFondos());
            new UsuariosBD().actualizarUsuario(usuario1);
            new ProductosBD().actualizarUnidades(producto);
            
            //Añadir datos de la compra a la factura
            String nombreFichero = "src/facturas/" + usuario1.getUsuario() + ".txt";
            
            try{
                //Creamos el nuevo fichero
                File factura = new File(nombreFichero);
                BufferedWriter escribirFactura = new BufferedWriter(new FileWriter(factura, true));
                
                //Generar codigoFactura
                String codigo = codigoAleatorio() + tresPrimerasLetras(producto);
                
                //Fecha hoy
                Date fechaActual = new Date();
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                
                //Añadir nuevos datos a la factura
                String datosFactura = (formatoFecha.format(fechaActual) + "|" + usuario1.getUsuario() + "|" + producto.getNombre() + "|" + producto.getGenero() + "|" + producto.getCategoria() + "|" + producto.getPrecio() + "|" + codigo);
                escribirFactura.write(datosFactura);
                escribirFactura.newLine();
                
                escribirFactura.close();
            } catch (IOException e){
                e.printStackTrace();
                return "error";
            }
            return "El producto se ha comprado con exito";
        }
    }
    
    //Devolver EDAD
    public int devolverEdad(Usuario usuario) {
        try {
            //Formato Fecha
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            //Pasar la fecha de nacimiento al formato DATE dado
            Date fechaNacimiento = dateFormat.parse(usuario.getNacimiento());
            
            //Recoger fecha actual
            Date fechaActual = new Date();
            
            //Establece los datos anteriormente como un objecto calendar
            Calendar calFechaNacimiento = Calendar.getInstance();
            calFechaNacimiento.setTime(fechaNacimiento);
            
            //Crear un objecto calendar y obtener la fecha actual del sistema
            Calendar calFechaActual = Calendar.getInstance();
            calFechaActual.setTime(fechaActual);

            //Recoger edad por el año
            int edad = calFechaActual.get(Calendar.YEAR) - calFechaNacimiento.get(Calendar.YEAR);

            //Calcular edad segun el mes y dia
            if (calFechaActual.get(Calendar.MONTH) < calFechaNacimiento.get(Calendar.MONTH) || (calFechaActual.get(Calendar.MONTH) == calFechaNacimiento.get(Calendar.MONTH) && calFechaActual.get(Calendar.DAY_OF_MONTH) < calFechaNacimiento.get(Calendar.DAY_OF_MONTH))) {
                edad--;
            }

            return edad;
        } catch (ParseException ex) {
            return 0;
        }
    }
    
    //Generar Codigo Aleatorio
    public String codigoAleatorio() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int digito = random.nextInt(10);
            sb.append(digito);
        }

        return sb.toString();
    }
    
    //Reoger las tres primeras letras del producto
    public String tresPrimerasLetras(Producto producto) {
        String nombreProducto = producto.getNombre();
        String tresPrimerasLetras = nombreProducto.substring(0, Math.min(nombreProducto.length(), 3));
        
        return tresPrimerasLetras;
    }
    
    public ArrayList<String[]> obtenerDatosFactura(Usuario usuario){
        String nombreFichero = "src/facturas/" + usuario.getUsuario() + ".txt";
        
        File fichero = new File(nombreFichero);
        if (fichero.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
                ArrayList<String[]> datos = new ArrayList<>();
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] fila = linea.split("\\|");
                    datos.add(fila);
                }
                
                return datos;
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            
        }else {
            return null;
        }
        
        return null;
    }  

    
}
