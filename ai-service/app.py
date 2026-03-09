from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline

app = FastAPI()

# modèle plus léger (~300MB)
MODEL_NAME = "MoritzLaurer/mDeBERTa-v3-base-mnli-xnli"

classifier = pipeline(
    "zero-shot-classification",
    model=MODEL_NAME
)

TYPE_LABELS = [
    "CARTE_BLOQUEE",
    "PAIEMENT_REFUSE",
    "DOUBLE_DEBIT",
    "RETRAIT_GAB",
    "FRAUDE_SUSPECTEE",
    "AUTRE"
]

CANAL_LABELS = [
    "CARTE",
    "GAB",
    "E_COMMERCE",
    "AGENCE",
    "AUTRE"
]

class ClassifyRequest(BaseModel):
    description: str


@app.post("/classify")
def classify(req: ClassifyRequest):

    text = req.description.strip()

    type_result = classifier(text, TYPE_LABELS)
    predicted_type = type_result["labels"][0]
    type_score = float(type_result["scores"][0])

    canal_result = classifier(text, CANAL_LABELS)
    predicted_canal = canal_result["labels"][0]
    canal_score = float(canal_result["scores"][0])

    suggested_title = predicted_type.replace("_", " ").title()

    confidence = (type_score + canal_score) / 2

    return {
        "predictedType": predicted_type,
        "predictedCanal": predicted_canal,
        "suggestedTitle": suggested_title,
        "confidenceScore": confidence,
        "modelVersion": MODEL_NAME
    }