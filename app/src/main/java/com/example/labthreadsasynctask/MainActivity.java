package com.example.labthreadsasynctask;

// --- Imports nécessaires pour Bitmap, AsyncTask, Handler, vues UI ---
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LAB 8 — Threads, AsyncTask & Handler
 * Auteur : Majesty | ENSA Marrakech — GCDSTE S4
 *
 * Objectif : démontrer que l'UI reste fluide grâce aux threads de fond.
 * Deux approches couvertes :
 *   → Approche 1 : Worker Thread + Handler (chargement d'image)
 *   → Approche 2 : AsyncTask (calcul lourd avec progression)
 */
public class MainActivity extends AppCompatActivity {

    // ── Références vers les composants du layout ──────────────────────
    private TextView  txtStatus;   // Affiche le statut du traitement
    private ProgressBar progressBar; // Barre de progression (0 → 100)
    private ImageView img;          // Affiche l'image chargée

    // ── Handler attaché au Main Thread ────────────────────────────────
    // Permet de poster du code sur l'UI Thread depuis un Worker Thread
    private Handler mainHandler;

    // ══════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ── Liaison vues XML ↔ variables Java ─────────────────────────
        txtStatus   = findViewById(R.id.txtStatus);
        progressBar = findViewById(R.id.progressBar);
        img         = findViewById(R.id.img);

        Button btnLoadThread = findViewById(R.id.btnLoadThread);
        Button btnCalcAsync  = findViewById(R.id.btnCalcAsync);
        Button btnToast      = findViewById(R.id.btnToast);

        // ── Initialisation du Handler lié au UI Thread ────────────────
        // Looper.getMainLooper() cible le thread principal de l'app
        mainHandler = new Handler(Looper.getMainLooper());

        // ── Bouton Toast : réponse immédiate même pendant un traitement ─
        // Sert à PROUVER que l'UI n'est pas bloquée
        btnToast.setOnClickListener(v ->
                Toast.makeText(
                        getApplicationContext(),
                        "✅ UI toujours réactive !",   // Message personnalisé
                        Toast.LENGTH_SHORT
                ).show()
        );

        // ── Bouton Thread : charge une image en arrière-plan ──────────
        btnLoadThread.setOnClickListener(v -> demarrerChargementImage());

        // ── Bouton AsyncTask : lance le calcul lourd avec progression ──
        btnCalcAsync.setOnClickListener(v -> new TacheCalculLourd().execute());
    }

    // ══════════════════════════════════════════════════════════════════
    // PARTIE 1 — Worker Thread + mainHandler.post()
    // But : charger une image sans figer l'écran
    // ══════════════════════════════════════════════════════════════════
    private void demarrerChargementImage() {

        // Étape A : on prépare l'UI AVANT de lancer le thread (on est sur UI Thread ici)
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(10); // Indication visuelle de démarrage
        txtStatus.setText("⏳ Chargement en cours via Thread...");

        // Étape B : création et démarrage du Worker Thread
        // Le code dans le lambda s'exécute EN ARRIÈRE-PLAN
        new Thread(() -> {

            // Simulation d'un délai réseau ou d'un traitement long
            try {
                Thread.sleep(1500); // 1.5 secondes d'attente simulée
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Décodage de l'image (mipmap du projet) — opération en fond
            Bitmap imageBitmap = BitmapFactory.decodeResource(
                    getResources(),
                    R.mipmap.ic_launcher_round  // Image ronde personnalisée
            );

            // Étape C : RETOUR sur le UI Thread via mainHandler.post()
            // Règle absolue : on ne modifie JAMAIS une View depuis un Worker Thread !
            mainHandler.post(() -> {
                img.setImageBitmap(imageBitmap);           // Affichage de l'image
                progressBar.setVisibility(View.INVISIBLE); // On cache la barre
                txtStatus.setText("🖼 Image chargée avec succès via Thread !");
            });

        }).start(); // .start() est indispensable — sans lui le thread ne démarre pas
    }

    // ══════════════════════════════════════════════════════════════════
    // PARTIE 2 — AsyncTask (approche pédagogique)
    // Paramètres generics : <EntréeTask, TypeProgression, TypeRésultat>
    //   Void    → aucun paramètre d'entrée pour execute()
    //   Integer → on publie la progression en pourcentage (0-100)
    //   Long    → le résultat final est un grand nombre entier
    // ══════════════════════════════════════════════════════════════════
    private class TacheCalculLourd extends AsyncTask<Void, Integer, Long> {

        // ── onPreExecute : appelé sur UI Thread AVANT le traitement ───
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            txtStatus.setText("🧮 Calcul lourd démarré (AsyncTask)...");
        }

        // ── doInBackground : s'exécute sur Worker Thread ───────────────
        // ⚠ INTERDIT de toucher l'UI ici → utiliser publishProgress()
        @Override
        protected Long doInBackground(Void... voids) {
            long total = 0; // Accumulateur du résultat

            // 100 itérations = 100% de progression
            for (int iteration = 1; iteration <= 100; iteration++) {

                // Simulation d'un calcul mathématique intensif
                for (int k = 0; k < 200_000; k++) {
                    total += (iteration * k) % 13; // Modulo 13 (différent de l'original)
                }

                // Envoie la progression au UI Thread → déclenche onProgressUpdate()
                publishProgress(iteration);
            }

            return total; // Valeur transmise à onPostExecute()
        }

        // ── onProgressUpdate : appelé sur UI Thread à chaque publishProgress() ──
        @Override
        protected void onProgressUpdate(Integer... valeurs) {
            // valeurs[0] contient le pourcentage publié par publishProgress()
            progressBar.setProgress(valeurs[0]);
            txtStatus.setText("🔄 Progression : " + valeurs[0] + "%");
        }

        // ── onPostExecute : appelé sur UI Thread quand doInBackground() termine ──
        @Override
        protected void onPostExecute(Long resultatFinal) {
            progressBar.setVisibility(View.INVISIBLE);
            txtStatus.setText("✅ Calcul terminé ! Résultat = " + resultatFinal);
        }
    }
}