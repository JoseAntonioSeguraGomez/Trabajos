package controlador;

import java.util.ArrayList;
import bbdd.ProductosBD;
import modelo.Producto;

public class ControladorProducto {
    
    //Obtener datos de la tabla producto
    public ArrayList<String[]> obtenerDatos(){
        return new ProductosBD().obtenerDatos();            
    }
       
    public String eliminarProducto(int id){
        //Llama al método de la base de datos para eliminar el producto por su ID
        boolean resultado = new ProductosBD().eliminarProducto(id);

        //Comprueba si el resultado de la eliminación es exitoso
        if(resultado == true){
            return "eliminado";
        }else{
            return "error";
        }
    }

    public ArrayList<String> AnadirProducto(Producto producto){
        //Creamos un array para almacenar el error y el texto
        ArrayList<String> error = new ArrayList<String>();
        
        //Comprueba si el producto ya existe en la base de datos
        String existe = new ProductosBD().ExisteProducto(producto);

        //Convierte los campos numéricos del producto a String
        String categoriaString = String.valueOf(producto.getCategoria());
        String precioString;
        String unidadesString;
        
        //Comprobaciones si un campo es menor o igaul 0
        if(producto.getPrecio() <= 0){
            precioString = ""; 
        }else{
            precioString = String.valueOf(producto.getPrecio());
        }
        
        if(producto.getUnidades() <= 0){
            unidadesString = ""; 
        }else{
            unidadesString = String.valueOf(producto.getUnidades());
        }

        //Verifica diferentes condiciones a la hora de añadir el producto
        if(existe != null){
            error.add("Existe");
            error.add("El producto ya existe");
            return error;
        }else if(producto.getNombre().equals("")){
            error.add("Nombre");
            error.add("El campo nombre está vacío");
            return error;        
        } else if(producto.getDescripcion().equals("")) {
            error.add("Descripcion");
            error.add("El campo descripción está vacío");
            return error;
        } else if(producto.getDescripcion().length() < 10) {
            error.add("Descripcion");
            error.add("La descripción es demasiado corta");
            return error;
        } else if(producto.getGenero().equals("")) {
            error.add("Genero");
            error.add("El campo género está vacío");
            return error;
        } else if(precioString.equals("")) {
            error.add("Precio");
            error.add("El campo precio está vacío");
            return error;
        }else if(unidadesString.equals("")) {
            error.add("Unidades");
            error.add("El campo unidades está vacío");
            return error;
        } else {
            //Llama al método de la base de datos para añadir el producto
            new ProductosBD().AnadirProducto(producto);
            error.add("Hecho");
            error.add("El producto se ha añadido correctamente");
            return error;
        }
    }

    public String ActualizarProductos(Producto producto){
        //Creamos un array para almacenar el error y el texto
        String error;
        
        //Convierte los campos numéricos del producto a String
        String categoriaString = String.valueOf(producto.getCategoria());
        String precioString;
        String unidadesString;
        
        //Comprobaciones si un campo es menor o igual a 0
        if(producto.getPrecio() <= 0){
            precioString = ""; 
        }else{
            precioString = String.valueOf(producto.getPrecio());
        }
        
        //Verifica diferentes condiciones a la hora de actualizar el producto
        if(producto.getNombre().equals("")){
            return ("El campo nombre está vacío");
        } else if(producto.getDescripcion().equals("")) {
            return ("El campo descripción está vacío");
        } else if(producto.getDescripcion().length() < 10) {
            return ("La descripción es demasiado corta");
        } else if(!(producto.getCategoria() == 3 || producto.getCategoria() == 7 || producto.getCategoria() == 12 || producto.getCategoria() == 18)){
            return ("La categoría es incorrecta");
        } else if(producto.getGenero().equals("")) {
            return ("El campo genero está vacío");
        } else if(precioString.equals("")) {
            return ("El campo precio está vacío");
        }else if(producto.getUnidades() < 0) {
            return ("El campo unidades es negativo");
        } else {
            //Llama a la base de datos para verificar si el producto existe a la hora de hacer el update
            error = new ProductosBD().EditarProducto(producto);
            return error;
        }
    }

    public ArrayList<String[]> MostrarFiltro(String nombre){
        //Llama al método de la base de datos para filtrar productos por el nombre introducido 
        return new ProductosBD().FiltrarContenido(nombre);
    }

    public ArrayList<String[]> OrdenarProductos(String ordenar){
        //Comprueba el tipo de orden seleccionado y llamar al método correspondiente de la base de datos
        if(ordenar.equals("ID")){
            return new ProductosBD().obtenerDatos();
        } else if(ordenar.equals("Nombre") || ordenar.equals("Genero") || ordenar.equals("Categoria")){
            return new ProductosBD().OrdenarPorFiltroMenor(ordenar);
        } else if(ordenar.equals("MayorPrecio")){
            return new ProductosBD().OrdenarPorFiltroMayor("precio");
        }  else if(ordenar.equals("MenorPrecio")){
            return new ProductosBD().OrdenarPorFiltroMenor("precio");
        }  else if(ordenar.equals("MayorUnidades")){
            return new ProductosBD().OrdenarPorFiltroMayor("Unidades");
        }  else if(ordenar.equals("MenorUnidades")){
            return new ProductosBD().OrdenarPorFiltroMenor("Unidades");
        } 

        return null;
    }

       
       

}
