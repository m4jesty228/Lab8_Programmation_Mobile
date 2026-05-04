# ⚡ LabThreadsAsyncTask 📱

> Application Android développée dans le cadre du **LAB 8 — Threads, AsyncTask & Handler**  
> Module : Programmation Mobile Android | ENSA Marrakech — GCDSTE S4

---

## 🎯 Objectifs pédagogiques

- Comprendre la différence entre **UI Thread** et **Worker Thread**
- Créer un thread de fond avec `new Thread()` et `Runnable`
- Mettre à jour l'interface graphique via `Handler(Looper.getMainLooper()).post()`
- Implémenter une **AsyncTask** avec progression en temps réel
- Éviter les erreurs classiques : UI bloquée, `CalledFromWrongThreadException`, ANR

---

## 🏗️ Structure du projet

```
LabThreadsAsyncTask/
├── java/
│   └── com.example.labthreadsasynctask/
│       └── MainActivity.java       # Activité principale + Thread + AsyncTask
└── res/
    └── layout/
        └── activity_main.xml       # Interface : TextView, ProgressBar, ImageView, Boutons
```

---

## ⚙️ Fonctionnement de l'application

| Action | Résultat attendu |
|---|---|
| Lancement de l'app | Interface affichée avec statut *"En attente d'une action..."* |
| Clic sur **Charger image (Thread)** | Un Worker Thread démarre, l'image se charge après 1.5s |
| Pendant le chargement → clic **Afficher Toast** | Toast immédiat ✅ → preuve que l'UI n'est pas bloquée |
| Clic sur **Calcul lourd (AsyncTask)** | ProgressBar avance de 0% à 100% en temps réel |
| Fin de l'AsyncTask | Résultat final affiché dans le TextView de statut |

---

## 🧩 Concepts clés illustrés

### 🔴 UI Thread (Main Thread)
Thread principal qui gère l'affichage et les clics. Toute opération longue ici → **ANR (Application Not Responding)**.

### 🟢 Worker Thread
Thread de fond créé manuellement avec `new Thread()`. Exécute les traitements lourds sans bloquer l'interface.

### 🔁 Retour au UI Thread
Trois solutions couvertes dans ce lab :
- `mainHandler.post(runnable)` — via `Handler(Looper.getMainLooper())`
- `view.post(runnable)` — via la vue elle-même
- `runOnUiThread(runnable)` — méthode de l'Activity

### 📊 AsyncTask (approche pédagogique)

```
onPreExecute()       → UI Thread   : préparation avant traitement
doInBackground()     → Worker Thread : calcul lourd (INTERDIT de toucher l'UI)
publishProgress()    → envoie la progression vers onProgressUpdate()
onProgressUpdate()   → UI Thread   : mise à jour de la ProgressBar
onPostExecute()      → UI Thread   : affichage du résultat final
```

---

## 🛠️ Technologies utilisées

| Élément | Détail |
|---|---|
| Langage | Java |
| Min SDK | API 21 (Android 5.0 Lollipop) |
| IDE | Android Studio |
| Librairies | Android SDK natif uniquement |
| Concurrence | `Thread`, `Handler`, `AsyncTask` |

---
## 🧪 Scénario de test (validation)

1. ▶️ Lancer l'app
2. Cliquer **"🖼 Charger image (Thread)"**
3. Immédiatement cliquer **"💬 Afficher Toast"**  
   → ✅ Le Toast doit apparaître sans délai (UI non bloquée)
4. Cliquer **"🧮 Calcul lourd (AsyncTask)"**  
   → ✅ La ProgressBar avance progressivement de 0% à 100%
5. Pendant l'AsyncTask, cliquer à nouveau **"💬 Afficher Toast"**  
   → ✅ Toujours réactif
---
## Démonstration
> 
https://github.com/user-attachments/assets/288f26b1-c749-4877-87f5-12f15ce73ae4

---

## 👤 Auteur

**DOSSAH Landry** 
ENSA Marrakech | GCDSTE S4    
Module : Programmation Mobile Android — Java
