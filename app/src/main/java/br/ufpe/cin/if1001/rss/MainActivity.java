package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private final String RSS_FEED = "http://rss.cnn.com/rss/edition.rss";
    private TextView conteudoRSS;
    private Button atualizarB;
    private ListView lv_rss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conteudoRSS = findViewById(R.id.conteudoRSS);
        atualizarB  = findViewById(R.id.atualizarB);

        lv_rss = findViewById(R.id.lv_rss);
/*
        ArrayAdapter<ItemRSS> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                itensRss
        );

        lv_rss.setAdapter(adapter);
*/
        atualizarB.setOnClickListener(
            new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    new RSSTask().execute(RSS_FEED);
                }
            }
        );
    }
/* do alfabeta
    public void inicializaItemRSSAdapter (ArrayList<ItemRSS> listaDeAtividades){

        this.atividadeAdapter = new AtividadeAdapter(this, listaDeAtividades, onClickAtividade());

        this.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        this.recyclerView.setAdapter(atividadeAdapter);

        atividadeAdapter.getItemCount();
    }
*/
    @Override
    protected void onStart() {
        super.onStart();

        new RSSTask().execute(RSS_FEED);

    }
    protected String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }


    private class RSSTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String feed = getRssFeed(strings[0]);

                return feed;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            //super.onPostExecute(s);
            ParserRSS parser = new ParserRSS();
            try {
                conteudoRSS.setText(parser.parse(s).toString());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}



