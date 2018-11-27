package com.example.usuario.ejemplosaccesodatosexternos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {
Button buttonCSV, buttonXML, buttonJSON;
ListView listView;
ProgressDialog progressDialog = null;
static String SERVIDOR = "http://192.168.1.48/scripts/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCSV = findViewById(R.id.buttonCSV);
        buttonXML =findViewById(R.id.buttonXML);
        buttonJSON = findViewById(R.id.buttonJSON);
        listView = findViewById(R.id.lista);




        buttonCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              DescargarCSV descargarCSV = new DescargarCSV();
              descargarCSV.execute("consultaCSV.php");



            }
        });


        buttonXML.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescargarXML descargarXML = new DescargarXML();
                descargarXML.execute("consultaXML.php");
            }
        });


    }

    private class DescargarXML extends AsyncTask<String, Void, Void>{

        List<String> list = new ArrayList<String>();
        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            String url = SERVIDOR+ script;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(url).openStream());

            Element raiz = doc.getDocumentElement();
            System.out.println("Raíz: " + raiz.getNodeName());
            NodeList hijos = raiz.getChildNodes();

            for (int i = 0; i < hijos.getLength(); i++) {
                Node nodo = hijos.item(i);
                if (nodo instanceof Element) {
                    NodeList nietos = nodo.getChildNodes();
                    String[] fila = new String[nietos.getLength()];
                    System.out.println("" + nodo.getNodeName());
                    System.out.println("Nietos:" + nietos.getLength());
                    int numero = 0;
                    for (int j = 0; j < nietos.getLength(); j++) {
                        if (nietos.item(j) instanceof Element) {

                            fila[numero] = nietos.item(j).getTextContent();
                            numero++;
                            System.out.println("" + nietos.item(j).getNodeName() + " " + nietos.item(j).getTextContent());
                        }
                    }
                    //modelo.addRow(fila);
                    numero = 0;

                }
            }

        } catch (ParserConfigurationException ex) {
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (SAXException ex) {
        }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;

            adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando datos...");
            progressDialog.show();

        }
    }

    private class DescargarCSV extends AsyncTask<String, Void, Void>{
        String total ="";
        @Override
        protected Void doInBackground(String... strings) {
            String script = strings[0];
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            URL url = null;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(SERVIDOR+script);
                System.out.println(SERVIDOR+script);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader br = null;

                    br = new BufferedReader(new InputStreamReader(inputStream));

                    String linea;

                    while ((linea = br.readLine()) != null) {
                        total += linea+"\n";
                    }

                    br.close();
                    inputStream.close();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ArrayAdapter<String> adapter;
            List<String> list = new ArrayList<String>();

            String[] lineas = total.split("\n");
            for (String lin: lineas){
                String[] campos = lin.split(",");
                String registro = "";
                registro = "ID: "+ campos[0];
                registro += "NOMBRE: "+campos[1];
                registro += "APELLIDOS: "+campos[2];
                registro += "TEL: "+campos[3];
                list.add(registro);

            }

            adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);
            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando datos...");
            progressDialog.show();

        }
    }
}
