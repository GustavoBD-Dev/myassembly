public class Tokens{
    //Atributos 
    private int cantidad;       // cantidad de elementos 
    private String nombreID;    // nombre del elemento 
    private String tipo;        // tipo al que pertenece 
    private String tamanio;     // tamaño en Bytes 
    private String valor;       // valor del tamaño en H
    public Tokens(int cantidadx, String nombreIdx, String tipox, String tamaniox, String valorx){
        this.cantidad = cantidadx;  //new  
        this.nombreID = nombreIdx;
        this.tipo = tipox;
        this.tamanio = tamaniox;
        this.valor = valorx;
    }

    public int getCantidad(){
        return cantidad;
    }

    public void setCantidad(int aumento){
        this.cantidad = this.cantidad + aumento;
    }

    public String getNombreID() {
        return nombreID;
    }

    public void setNombreID(String nombreID) {
        this.nombreID = nombreID;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTamanio() {
        return tamanio;
    }

    public void setTamanio(String tamanio) {
        this.tamanio = tamanio;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return ((this.cantidad<10)? " "+this.cantidad +"\t" : this.cantidad + "\t")
            + this.nombreID.trim() + ((this.nombreID.length()>7)?"\t":"\t\t")
            + this.tipo.trim() + "\t" + this.tamanio.trim() + ((this.tamanio.equals("------")?"":" Bytes"))+ 
            ((this.tamanio.length()>12)?"\t":"\t\t") + this.valor.trim() ;
    }

}