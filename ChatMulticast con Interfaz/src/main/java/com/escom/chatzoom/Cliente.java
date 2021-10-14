/*Comentarios*/
package com.escom.chatzoom;

import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.text.BadLocationException;

public class Cliente {

    private static boolean cerrado = true;

    /*Tarjeta de Red, NIC */
 /* static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.printf("Interfaz: %s\n", netint.getDisplayName());
        System.out.printf("Nombre: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            System.out.printf("InetAddress: %s\n", inetAddress);
        }
        System.out.printf("\n");
    }
     */
    public static void main(String[] args) throws IOException, BadLocationException {
        String name = JOptionPane.showInputDialog(null, "Ingresa tu nombre de usuario", "Registro", JOptionPane.QUESTION_MESSAGE);
        name = name.trim();
        Interfaz Ventana = new Interfaz(name);
        //String enviarNombre = "<inicio>"+name; 
        
        Ventana.setVisible(true);

        Ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent WWE) {
                cerrado = false;
            }
        });

        int pto = 4000;
        String hhost = "230.1.1.1";
        SocketAddress remote = null;
        try {
            try {
                remote = new InetSocketAddress(hhost, pto);
            } catch (Exception e) {
                e.printStackTrace();
            }//catch
            /*Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets))
                displayInterfaceInformation(netint);*/
            NetworkInterface ni = NetworkInterface.getByName("wlp3s0");
            DatagramChannel cl = DatagramChannel.open(StandardProtocolFamily.INET); //buscar que es
            InetAddress group = InetAddress.getByName("230.1.1.1");

            cl.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            cl.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            cl.join(group, ni);
            cl.configureBlocking(false);
            Selector sel = Selector.open();

            InetSocketAddress dir = new InetSocketAddress(pto);
            cl.socket().bind(dir);
            cl.register(sel, SelectionKey.OP_READ);

            while (cerrado) {
                ByteBuffer b = ByteBuffer.allocate(100);
                sel.select();
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey k = (SelectionKey) it.next();
                    it.remove();
                    if (k.isReadable()) {
                        DatagramChannel ch = (DatagramChannel) k.channel();
                        b.clear();
                        SocketAddress descomprimir = ch.receive(b);
                        b.flip();
                        //ch.send(b, remote);
                        String recibido = new String(b.array(), StandardCharsets.UTF_8);
                        System.out.println("Datagrama Recibido: " + recibido);
                        if (recibido.contains("<inicio>")) {
                            String nombre = recibido.replace("<inicio>", "");
                            if(!nombre.equals(Ventana.nombreuser)){
                                //Se sabe quien manda el mensaje<inicio> y no soy yo
                                if(!Ventana.usuarioExiste(nombre)){
                                    /*
                                       Si se trata de alguien nuevo, le mando un <inicio> Nombre del user
                                        para que me registre en su lista
                                    */
                                 String enviarNombre = "<inicio>"+Ventana.nombreuser;
                                 //Una vex enviado tu nombre, registras esa persona en la sala.
                                 Ventana.IngresarSala(nombre);
                                }
                            
                            }
                            //Ventana.IngresarSala(recibido.replace("<inicio>", ""));
                        } else if (recibido.contains("<msj>")) {
                            String nombre = recibido.substring(recibido.lastIndexOf("<") + 1, recibido.lastIndexOf(">"));
                            Ventana.enviarMensaje(recibido.replace("<msj>", ""), nombre);
                        } else if (recibido.contains("<privado>")) {
                            String nombre = recibido.substring(recibido.lastIndexOf("<"), recibido.lastIndexOf(">") + 1);
                            recibido = recibido.replace(nombre, "");
                            String nombre2 = recibido.substring(recibido.lastIndexOf("<") + 1, recibido.lastIndexOf(">"));
                            nombre = nombre.replace("<", "");
                            nombre = nombre.replace(">", "");
                            Ventana.enviarPrivado(recibido.replace("<privado>", ""), nombre, nombre2);

                        } else if (recibido.contains("<fin>")) {
                            Ventana.SalirSala(recibido.replace("<fin>", ""));
                        }
                        continue;
                    }
                }//while iterator
            }//while Readable
            cl.close();
            //System.out.println("Termina envio de datagramas");
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main
}
