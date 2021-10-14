/*Comentarios*/
package com.escom.chatzoom;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 *
 * @author marquez
 */
public class Interfaz extends javax.swing.JFrame {

    JButton Send;
    JTextField Message;
    JTextPane Panelchido;
    HTMLEditorKit emojis;
    String nombreuser;
    ArrayList usuarios = new ArrayList();
    DefaultListModel modelo = new DefaultListModel();
    boolean wavingFlag = false;

    public Interfaz(String nombre) throws IOException, BadLocationException {
        initComponents();
        this.setTitle("Chat:" + nombre);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        ListaUsuarios.setModel(modelo);
        Send = this.Enviar;
        Message = this.Mensaje;
        Panelchido = this.jTextPane1;
        emojis = new HTMLEditorKit();
        Panelchido.setEditorKit(emojis);
        nombreuser = nombre;
        //IngresarSala(nombreuser);
    }
    
    public boolean usuarioExiste(String nombre){
        return modelo.contains(nombre);
    }

    public void IngresarSala(String name) throws IOException, BadLocationException {
        String codigoHTML = "<font color=\"green\">El usuario " + name + " ha entrado al chat</font>";
        Document doc = Panelchido.getDocument();
        emojis.insertHTML((HTMLDocument) doc, doc.getLength(), codigoHTML, 0, 0, null);
        usuarios.add(name);
        modelo.removeAllElements();
        for (int i = 0; i < usuarios.size(); i++) {
            modelo.addElement(usuarios.get(i));
            System.out.println(usuarios);
        }
    }

    public void SalirSala(String name) throws IOException, BadLocationException {
        String codigoHTML = "<font color=\"red\">El usuario " + name + " sale del chat</font>";
        Document doc = Panelchido.getDocument();
        emojis.insertHTML((HTMLDocument) doc, doc.getLength(), codigoHTML, 0, 0, null);
        usuarios.remove(name);
        modelo.removeElement(name);
    }

    public void enviarMensaje(String texto, String user) throws IOException, BadLocationException {
        texto = Remplazar(texto);
        String codigoHTML = null;
        if (user.equals(nombreuser)) {
            codigoHTML = "<font color=\"black\">" + nombreuser + ":" + texto + "</font>";
        } else {
            codigoHTML = "<div style=\"text-align:right;\"><font color=\"black\">" + user + ":" + texto + "</font></div>";
        }
        Document doc = Panelchido.getDocument();
        emojis.insertHTML((HTMLDocument) doc, doc.getLength(), codigoHTML, 0, 0, null);
    }

    public void enviarPrivado(String texto, String nombre, String user) throws IOException, BadLocationException {
        texto = Remplazar(texto);
        String codigoHTML = null;

        if (user.equals(nombreuser)) {
            codigoHTML = "<font color=\"black\">" + user + "(privado):" + texto + "</font>";
            Document doc = Panelchido.getDocument();
            emojis.insertHTML((HTMLDocument) doc, doc.getLength(), codigoHTML, 0, 0, null);
        } else if (nombre.equals(nombreuser)) {
            codigoHTML = "<div style=\"text-align:right;\"><font color=\"black\">" + user + "(privado):" + texto + "</font></div>";
            Document doc = Panelchido.getDocument();
            emojis.insertHTML((HTMLDocument) doc, doc.getLength(), codigoHTML, 0, 0, null);
        }
    }

    private String Remplazar(String texto) {
        if (texto.contains(":)")) {
            texto = texto.replace(":)", "<img src=\"file:img/feliz.png\" width=\"100\" heigth=\"100\" />");
        } else if (texto.contains("_loco_")) {
            texto = texto.replace("_loco_", "<img src=\"file:img/loco.gif\" width=\"200\" heigth=\"200\" />");
        }else if (texto.contains(":o")) {
            texto = texto.replace(":o", "<img src=\"file:img/amor.png\" width=\"100\" heigth=\"100\" />");
        }else if (texto.contains(":S")) {
            texto = texto.replace(":S", "<img src=\"file:img/enojo.png\" width=\"100\" heigth=\"100\" />");
        }else if (texto.contains("_homero_")) {
            texto = texto.replace("_homero_", "<img src=\"https://i.pinimg.com/originals/21/1e/10/211e108b335a044777abdc30679b5a54.gif\" width=\"200\" heigth=\"200\" />");
        }
        return texto;
    }

    private void Enviar() {
        String contenido = Message.getText();
        String validacion = contenido.substring(contenido.indexOf("<"), contenido.lastIndexOf(">") + 1);
        String nombre = null;

        System.out.println(validacion);

        String tag = contenido.substring(contenido.indexOf("<"), contenido.indexOf(">") + 1);

        if (tag.equals("<privado>")) {
            nombre = validacion.substring(contenido.lastIndexOf("<"), contenido.lastIndexOf(">") + 1);
            System.out.println(nombre);
        }

        if (contenido.equals("<inicio>" + nombreuser)) {
            wavingFlag = true;
        } else if (validacion.equals("<msj><" + nombreuser + ">") || validacion.equals("<privado><" + nombreuser + ">" + nombre)) {
            wavingFlag = true;
        } else if (contenido.equals("<fin>" + nombreuser)) {
            wavingFlag = true;
        } else {
            JOptionPane.showMessageDialog(null, "Etiqueta invalida", "Error", JOptionPane.ERROR_MESSAGE);
            wavingFlag = false;
        }

        if (wavingFlag) {
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
                cl.register(sel, SelectionKey.OP_WRITE);

                int n = 0;

                ByteBuffer b = ByteBuffer.allocate(100);
                sel.select();
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey k = (SelectionKey) it.next();
                    it.remove();
                    if (k.isWritable()) {
                        DatagramChannel ch = (DatagramChannel) k.channel();
                        b.clear();
                        b.put(Message.getText().getBytes());
                        b.flip();
                        ch.send(b, remote);
                        //String recibido = new String(b.array(), StandardCharsets.UTF_8);
                        System.out.println("Datagrama Enviado:");
                        continue;
                    }
                }//while iterator
                cl.close();
                Message.setText("");
                //System.out.println("Termina envio de datagramas");
            } catch (Exception e) {
                e.printStackTrace();
            }//catch
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Enviar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        Mensaje = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListaUsuarios = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Enviar.setText("Enviar");
        Enviar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EnviarMouseClicked(evt);
            }
        });

        jTextPane1.setEditable(false);
        jTextPane1.setContentType("text/html"); // NOI18N
        jTextPane1.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jScrollPane1.setViewportView(jTextPane1);

        Mensaje.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N

        jScrollPane2.setViewportView(ListaUsuarios);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Mensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Enviar))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Mensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Enviar))
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jScrollPane1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void EnviarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EnviarMouseClicked
        Enviar();
    }//GEN-LAST:event_EnviarMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Enviar;
    private javax.swing.JList<String> ListaUsuarios;
    private javax.swing.JTextField Mensaje;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
