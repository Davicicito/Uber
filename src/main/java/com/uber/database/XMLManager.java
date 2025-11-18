package com.uber.database;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XMLManager {

    /**
     * Escribe un objeto Java en un archivo XML.
     *
     * @param objeto   El objeto que se va a convertir a XML.
     * @param fileName Nombre del archivo de salida.
     * @param <T>      Tipo genérico del objeto.
     * @return true si se escribió correctamente, false si hubo error.
     */
    public static <T> boolean writeXML(T objeto, String fileName) {
        boolean result = false;
        try {
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(objeto, new File(fileName));

            result = true;

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    /**
     * Lee un archivo XML y lo convierte en un objeto Java.
     *
     * @param objeto   Instancia del tipo esperado (para obtener la clase).
     * @param fileName Nombre del archivo XML de entrada.
     * @param <T>      Tipo del objeto que se quiere obtener.
     * @return El objeto leído del XML, o null si hubo error.
     */
    @SuppressWarnings("unchecked")
    public static <T> T readXML(T objeto, String fileName) {
        T result = null;

        try {
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();

            result = (T) unmarshaller.unmarshal(new File(fileName));

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
