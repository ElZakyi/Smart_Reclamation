from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util

app = FastAPI()

model = SentenceTransformer("paraphrase-multilingual-MiniLM-L12-v2")

# =========================
# EXEMPLES POUR TYPE
# =========================
print("MY NEW AI SERVICE LOADED")
TYPE_EXAMPLES = {

"RETRAIT_GAB":[
"impossible de retirer de l'argent au gab",
"le distributeur ne donne pas l'argent",
"problème de retrait au distributeur",
"le gab a avalé ma carte",
"je ne peux pas retirer de l'argent",
"le distributeur refuse mon retrait",
"le gab affiche une erreur lors du retrait",
"le distributeur automatique ne fonctionne pas",
"le retrait a échoué",
"le gab n'a pas délivré l'argent",
"le distributeur est hors service",
"le retrait au distributeur est impossible",
"ma carte est restée dans le gab",
"le distributeur a bloqué ma carte",
"le retrait d'argent ne marche pas"
],

"PAIEMENT_REFUSE":[
"paiement refusé avec ma carte",
"ma carte est refusée en magasin",
"je ne peux pas payer avec ma carte",
"le terminal refuse ma carte",
"paiement refusé au magasin",
"la transaction est refusée",
"ma carte ne passe pas au paiement",
"paiement impossible avec ma carte",
"ma carte bancaire est refusée",
"le paiement par carte ne fonctionne pas",
"le terminal de paiement affiche erreur",
"la transaction par carte est refusée"
],

"DOUBLE_DEBIT":[
"j'ai été débité deux fois",
"double débit sur mon compte",
"paiement prélevé deux fois",
"le montant a été débité deux fois",
"je vois deux débits pour le même paiement",
"la transaction apparaît deux fois",
"j'ai un double paiement",
"mon compte a été débité deux fois",
"le paiement a été facturé deux fois",
"la même transaction est répétée"
],

"FRAUDE_SUSPECTEE":[
"transaction que je n'ai jamais faite",
"paiement suspect sur mon compte",
"quelqu'un utilise ma carte sans autorisation",
"je ne reconnais pas cette transaction",
"un paiement inconnu apparaît",
"une transaction frauduleuse sur mon compte",
"je vois un achat que je n'ai pas fait",
"mon compte a une transaction suspecte",
"un paiement que je n'ai pas autorisé",
"un achat inconnu sur ma carte",
"un paiement frauduleux",
"activité suspecte sur mon compte"
],

"PROBLEME_ECOMMERCE":[
"paiement refusé sur internet",
"je ne peux pas payer sur un site",
"erreur lors du paiement en ligne",
"paiement impossible sur un site web",
"transaction refusée sur internet",
"paiement en ligne échoué",
"erreur lors d'un achat en ligne",
"impossible de payer sur un site e commerce",
"le paiement sur internet est bloqué",
"le site refuse mon paiement"
],

"ERREUR_FACTURATION":[
"montant débité incorrect",
"erreur de facturation bancaire",
"le montant du paiement est faux",
"le montant débité est différent",
"erreur dans le montant du paiement",
"la somme débitée est incorrecte",
"le prix prélevé est incorrect",
"mauvais montant facturé",
"erreur dans le débit bancaire",
"le montant affiché ne correspond pas"
],

"AUTRE":[
"problème avec mon compte",
"je veux contacter le support",
"autre problème bancaire",
"j'ai une question concernant mon compte",
"je veux signaler un problème",
"besoin d'aide pour mon compte",
"demande d'information bancaire"
]

}

# =========================
# EXEMPLES POUR CANAL
# =========================

CANAL_EXAMPLES = {

"GAB":[
"retrait au distributeur",
"problème au gab",
"distributeur automatique",
"retrait au distributeur automatique",
"transaction au distributeur",
"gab bancaire",
"machine de retrait",
"distributeur de billets"
],

"CARTE":[
"paiement avec carte",
"paiement en magasin",
"terminal de paiement",
"paiement par carte bancaire",
"paiement avec ma carte",
"transaction par carte",
"carte bancaire utilisée",
"paiement sur terminal"
],

"E_COMMERCE":[
"paiement sur internet",
"achat en ligne",
"site e commerce",
"paiement sur un site",
"achat sur internet",
"transaction en ligne",
"paiement sur un site web",
"achat sur une boutique en ligne"
],

"E_BANKING":[
"application bancaire",
"e banking",
"application mobile banque",
"mobile banking",
"application de la banque",
"paiement depuis l'application",
"transaction via application"
],

"AUTRE":[
"autre canal",
"autre moyen",
"autre situation"
]

}

# =========================
# EXEMPLES PRIORITY
# =========================

PRIORITY_EXAMPLES = {

    "LOW":[
    "petit problème",
    "demande simple",
    "information",
    "question simple",
    "demande d'information",
    "petite erreur"
    ],

    "AVERAGE":[
    "problème normal",
    "problème bancaire",
    "transaction bloquée",
    "problème de paiement",
    "carte refusée"
    ],

    "HIGH":[
    "paiement bloqué",
    "argent bloqué",
    "carte bloquée",
    "je ne peux plus payer",
    "transaction importante bloquée"
    ],

    "CRITICAL":[
    "fraude",
    "transaction suspecte",
    "urgence",
    "paiement frauduleux",
    "activité suspecte",
    "compte piraté"
]

}
# =========================
# PRECALCUL EMBEDDINGS
# =========================

def build_embeddings(examples):

    embeddings = {}

    for label,phrases in examples.items():

        embeddings[label] = model.encode(
            phrases,
            convert_to_tensor=True
        )

    return embeddings


TYPE_EMB = build_embeddings(TYPE_EXAMPLES)
CANAL_EMB = build_embeddings(CANAL_EXAMPLES)
PRIORITY_EMB = build_embeddings(PRIORITY_EXAMPLES)

# =========================

class ClassifyRequest(BaseModel):
    description:str

# =========================

def predict(text,embeddings):

    text_emb = model.encode(text,convert_to_tensor=True)

    best_label = None
    best_score = -1

    for label,emb in embeddings.items():

        score = util.cos_sim(text_emb,emb).max()

        if score > best_score:

            best_score = score
            best_label = label

    return best_label,float(best_score)

# =========================

@app.post("/classify")
def classify(req:ClassifyRequest):

    text = req.description.lower()

    type_pred,type_score = predict(text,TYPE_EMB)

    canal_pred,canal_score = predict(text,CANAL_EMB)

    priority_pred,priority_score = predict(text,PRIORITY_EMB)

    confidence = (type_score+canal_score+priority_score)/3

    title = type_pred.replace("_"," ").title()

    return{

        "predictedType":type_pred,
        "predictedCanal":canal_pred,
        "predictedPriority":priority_pred,
        "suggestedTitle":title,
        "confidenceScore":confidence,
        "modelVersion":"sentence-transformers"
    }