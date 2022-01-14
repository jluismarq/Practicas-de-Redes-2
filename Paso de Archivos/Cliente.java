
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author marquez
 */
public class Cliente {

    public static void main(String[] args) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Socket cl = new Socket(InetAddress.getLocalHost(), 7000);

            int buffer = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el tamaño del buffer: "));

            cl.setSendBufferSize(buffer);
            cl.setReceiveBufferSize(buffer);

            System.out.println("El tamaño del buffer de envio de paquetes es  :" + cl.getSendBufferSize());
            System.out.println("El tamaño del buffer para recibir paquetes es :" + cl.getReceiveBufferSize());

            System.out.println("¿Desea activar el algoritmo de nagle? (s/n)");
            String respuesta = br.readLine();

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

            dos.writeInt(buffer);
            dos.flush();
            
            //Esta madre del TCP No Delay es el algoritmo de Neagle 
            
            if ("si".equalsIgnoreCase(respuesta) || "s".equalsIgnoreCase(respuesta)) {
                cl.setTcpNoDelay(true);
                dos.writeBoolean(true);
            } else {
                cl.setTcpNoDelay(false);
                dos.writeBoolean(false);
            }
            dos.flush();

            System.out.println("¿Algoritmo de nagle activado? " + cl.getTcpNoDelay());

            JFileChooser jf = new JFileChooser(".");
            jf.setMultiSelectionEnabled(true);

            int r = jf.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File[] f = jf.getSelectedFiles();  //Manejador

                dos.writeInt(f.length);
                dos.flush();

                String[] direcciones = new String[f.length]; //Direcciones
                String[] nombres = new String[f.length]; //Nombres
                long[] tam = new long[f.length];  //Tamaño

                for (int i = 0; i < f.length; i++) {
                    nombres[i] = f[i].getName();
                    tam[i] = f[i].length();
                    direcciones[i] = f[i].getAbsolutePath();
                    dos.writeUTF(f[i].getName());
                    dos.flush();
                    dos.writeLong(f[i].length());
                    dos.flush();
                }

                for (int i = 0; i < f.length; i++) {
                    DataInputStream dis = new DataInputStream(new FileInputStream(direcciones[i]));
                    System.out.println("Enviando archivo: " + nombres[i]);
                    byte[] b = new byte[buffer];
                    long enviados = 0;
                    int porcentaje, n;

                    while (enviados < tam[i] ) {
                        n = dis.read(b);
                        dos.writeInt(n);
                        dos.flush();
                        dos.write(b, 0, n);
                        enviados = enviados + n;
                        dos.flush();
                        porcentaje = (int) (enviados * 100 / tam[i]);
                        System.out.print("\nEnviado: " + porcentaje + "%\r");
                    }//While
                    System.out.println("\n\nArchivo enviado" + nombres[i] + " enviado");
                    dis.close();
                }//fin del for

                dos.close();
                cl.close();
            }//if
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
