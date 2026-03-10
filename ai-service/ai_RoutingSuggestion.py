from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util

app = FastAPI()

print("ROUTING AI SERVICE LOADED")

model = SentenceTransformer("paraphrase-multilingual-MiniLM-L12-v2")


# =========================
# TEAM EXAMPLES
# =========================

TEAM_EXAMPLES = {

"MONETIQUE":[
"carte avalée au distributeur",
"retrait impossible au gab",
"distributeur ne donne pas l'argent",
"carte bloquée au distributeur",
"problème de retrait",
"gab en panne",
"retrait refusé"
],

"PAIEMENT":[
"paiement refusé",
"carte refusée en magasin",
"transaction refusée",
"terminal refuse la carte",
"paiement impossible avec ma carte"
],

"FRAUDE":[
"transaction que je n'ai pas faite",
"paiement frauduleux",
"transaction suspecte",
"je ne reconnais pas cette opération",
"activité suspecte sur mon compte"
],

"E_COMMERCE":[
"paiement refusé sur internet",
"problème paiement en ligne",
"transaction refusée sur un site",
"achat en ligne impossible"
],

"SUPPORT":[
"question concernant mon compte",
"problème général bancaire",
"demande d'information",
"besoin d'aide"
]

}

STOPWORDS = {
"le","la","les","un","une","des","de","du","au","aux",
"et","ou","mais","donc","car","ni",
"je","tu","il","elle","nous","vous","ils",
"mon","ma","mes","ton","ta","tes",
"ce","cet","cette","ces",
"sur","dans","avec","pour","par"
}

def extract_keywords(text):

    words = text.lower().split()

    keywords = []

    for w in words:
        w = w.strip(".,!?;:")
        if len(w) > 3 and w not in STOPWORDS:
            keywords.append(w)

    return list(set(keywords))[:5]

# =========================
# EMBEDDINGS PRECALCUL
# =========================

def build_embeddings(examples):

    embeddings = {}

    for label,phrases in examples.items():

        embeddings[label] = model.encode(
            phrases,
            convert_to_tensor=True
        )

    return embeddings


TEAM_EMB = build_embeddings(TEAM_EXAMPLES)


# =========================

class RoutingRequest(BaseModel):

    description:str


# =========================

def predict_team(text):

    text_emb = model.encode(text,convert_to_tensor=True)

    best_label = None
    best_score = -1

    for label,emb in TEAM_EMB.items():

        score = util.cos_sim(text_emb,emb).max()

        if score > best_score:

            best_score = score
            best_label = label

    return best_label,float(best_score)


# =========================

@app.post("/routing")

def routing(req:RoutingRequest):

    text = req.description.lower()

    team,score = predict_team(text)
    keywords = extract_keywords(text)

    return{

        "team":team,
        "score":score,
        "keywords":keywords,
        "model":"sentence-transformers"

    }