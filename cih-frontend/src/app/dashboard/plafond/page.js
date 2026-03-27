"use client";

import { useEffect, useState } from "react";
import api from "@/services/api";

export default function PlafondPage() {

  const [cardForm, setCardForm] = useState({
    cardNumberMasked: "",
    cardType: "",
    currentLimit: ""
  });

  const [cards, setCards] = useState([]);
  const [message, setMessage] = useState("");
  const [editingCardId, setEditingCardId] = useState(null);

  // 🔥 NOUVEAU : gestion plafond
  const [selectedCardForPlafond, setSelectedCardForPlafond] = useState(null);

  const [plafondForm, setPlafondForm] = useState({
    newPlafond: "",
    justification: "",
  });
  const [showOtpInput, setShowOtpInput] = useState(false);
  const [otpCode, setOtpCode] = useState("");

  useEffect(() => {
    getCards();
  }, []);

  // =========================
  // GET CARDS
  // =========================
  const getCards = async () => {
    try {
      const currentUser = JSON.parse(localStorage.getItem("user"));
      const res = await api.get(`/card/user/${currentUser.idUser}`);
      setCards(res.data);
    } catch (error) {
      setMessage("Erreur récupération cartes");
    }
  };

  // =========================
  // CREATE / UPDATE CARD
  // =========================
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !cardForm.cardNumberMasked ||
      !cardForm.cardType ||
      !cardForm.currentLimit
    ) {
      setMessage("Veuillez remplir tous les champs !");
      return;
    }

    try {
      const currentUser = JSON.parse(localStorage.getItem("user"));

      if (editingCardId) {
        await api.put(`/card/${editingCardId}`, cardForm);
        setMessage("Carte modifiée avec succès !");
      } else {
        await api.post(`/card/${currentUser.idUser}`, cardForm);
        setMessage("Carte créée avec succès !");
      }

      setCardForm({
        cardNumberMasked: "",
        cardType: "",
        currentLimit: ""
      });

      setEditingCardId(null);
      await getCards();

    } catch (error) {
      setMessage("Erreur opération carte");
    }
  };

  // =========================
  // DELETE CARD
  // =========================
  const deleteCard = async (id) => {
    try {
      await api.delete(`/card/${id}`);
      setMessage("Carte supprimée !");
      await getCards();
    } catch (error) {
      setMessage("Erreur suppression");
    }
  };

  // =========================
  // EDIT CARD
  // =========================
  const editCard = (card) => {
    setCardForm({
      cardNumberMasked: card.cardNumberMasked,
      cardType: card.cardType,
      currentLimit: card.currentLimit
    });

    setEditingCardId(card.idCard);
  };

  // =========================
  // HANDLE PLAFOND FORM
  // =========================
  const handlePlafondChange = (e) => {
    setPlafondForm({
      ...plafondForm,
      [e.target.name]: e.target.value
    });
  };

  // =========================
  // SUBMIT PLAFOND REQUEST
  // =========================
  const submitPlafondRequest = async () => {
  try {
    const user = JSON.parse(localStorage.getItem("user"));

    const res = await api.post("/otp/generate", null, {
      params: {
        userId: user.idUser,
        cardId: selectedCardForPlafond.idCard,
        limit: plafondForm.newPlafond,
        justification: plafondForm.justification,
      },
    });

    setShowOtpInput(true);
    setMessage(res.data);

  } catch(error) {
    setMessage(error.response?.data?.error || "Erreur /POST génération OTP : " + error);
  }
};
const verifyOtp = async () => {
  try {
    const user = JSON.parse(localStorage.getItem("user"));

    const res = await api.post("/otp/verify", null, {
      params: {
        userId: user.idUser,
        code: otpCode,
      },
    });

    setMessage(res.data);
    setShowOtpInput(false);
    setSelectedCardForPlafond(null);
    setPlafondForm({
    newPlafond: "",
    justification: ""
    });

  } catch(error) {
    setMessage("❌ Code éxpiré ou incorrect");
  }
};

  return (
    <div className="p-6 max-w-4xl mx-auto space-y-6">

      <h1 className="text-2xl font-bold">
        Gestion des cartes
      </h1>

      {/* ========================= FORM ========================= */}
      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded-xl shadow space-y-4"
      >

        <input
          placeholder="Numéro de carte"
          value={cardForm.cardNumberMasked}
          onChange={(e) =>
            setCardForm({
              ...cardForm,
              cardNumberMasked: e.target.value
            })
          }
          className="w-full border p-2 rounded"
        />

        <select
          value={cardForm.cardType}
          onChange={(e) =>
            setCardForm({
              ...cardForm,
              cardType: e.target.value
            })
          }
          className="w-full border p-2 rounded"
        >
          <option value="">Choisir votre type de carte</option>
          <option value="MASTERCARD">Mastercard</option>
          <option value="VISA">Visa</option>
          <option value="SAYIDATI">Sayidati</option>
        </select>

        <input
          placeholder="Limite actuelle"
          type="number"
          value={cardForm.currentLimit}
          onChange={(e) =>
            setCardForm({
              ...cardForm,
              currentLimit: e.target.value
            })
          }
          className="w-full border p-2 rounded"
        />

        <button
          type="submit"
          className={`w-full py-2 rounded text-white ${
            editingCardId
              ? "bg-orange-600 hover:bg-orange-700"
              : "bg-blue-600 hover:bg-blue-700"
          }`}
        >
          {editingCardId ? "Modifier carte" : "Créer carte"}
        </button>

        {editingCardId && (
          <button
            type="button"
            onClick={() => {
              setEditingCardId(null);
              setCardForm({
                cardNumberMasked: "",
                cardType: "",
                currentLimit: ""
              });
            }}
            className="w-full bg-gray-400 text-white py-2 rounded"
          >
            Annuler modification
          </button>
        )}
      </form>
    
      {/* ========================= TABLE ========================= */}
      <div className="bg-white p-6 rounded-xl shadow">

        <h2 className="font-semibold mb-4">
          Mes cartes
        </h2>

        {cards.length > 0 ? (
          <table className="w-full border text-sm">

            <thead className="bg-gray-100">
              <tr>
                <th className="p-2">Id</th>
                <th className="p-2">Numéro</th>
                <th className="p-2">Type</th>
                <th className="p-2">Limite</th>
                <th className="p-2">Statut</th>
                <th className="p-2">Créé à</th>
                <th className="p-2">Actions</th>
              </tr>
            </thead>

            <tbody>
              {cards.map((card) => (
                <tr key={card.idCard} className="border text-center">

                  <td className="p-2">{card.idCard}</td>
                  <td className="p-2">{card.cardNumberMasked}</td>
                  <td className="p-2">{card.cardType}</td>
                  <td className="p-2">{card.currentLimit}</td>

                  <td className="p-2">
                    <span className={`px-2 py-1 rounded text-xs font-semibold
                      ${card.status === "ACTIVE"
                        ? "bg-green-100 text-green-700"
                        : "bg-red-100 text-red-700"}`}>
                      {card.status}
                    </span>
                  </td>

                  <td className="p-2">
                    {new Date(card.createdAt).toLocaleString()}
                  </td>

                  <td className="p-2 space-x-2">

                    <button
                      onClick={() => editCard(card)}
                      className="bg-yellow-500 text-white px-2 py-1 rounded"
                    >
                      Modifier
                    </button>

                    <button
                      onClick={() => deleteCard(card.idCard)}
                      className="bg-red-600 text-white px-2 py-1 rounded"
                    >
                      Supprimer
                    </button>

                    <button
                      onClick={() => setSelectedCardForPlafond(card)}
                      className="bg-indigo-600 text-white px-2 py-1 rounded"
                    >
                      Changer plafond
                    </button>

                  </td>

                </tr>
              ))}
            </tbody>

          </table>
        ) : (
          <p className="text-gray-500 text-sm">
            Aucune carte trouvée
          </p>
        )}
      </div>

      {/* ========================= FORM PLAFOND ========================= */}
      {selectedCardForPlafond && (
        <div className="bg-white p-6 rounded-xl shadow mt-6">

          <h2 className="font-semibold mb-3 text-indigo-700">
            💳 Changement de plafond
          </h2>

          <p className="text-sm mb-2">
            Carte : {selectedCardForPlafond.cardNumberMasked}
          </p>

          <input
            name="newPlafond"
            value={plafondForm.newPlafond}
            placeholder="Nouveau plafond"
            onChange={handlePlafondChange}
            className="w-full border p-2 rounded mb-2"
          />

          <textarea
            name="justification"
            value={plafondForm.justification}
            placeholder="Justification"
            onChange={handlePlafondChange}
            className="w-full border p-2 rounded mb-2"
          />

          <div className="flex gap-2">

            <button
              onClick={submitPlafondRequest}
              className="bg-indigo-600 text-white px-4 py-2 rounded"
            >
              Envoyer
            </button>

            <button
              onClick={() => setSelectedCardForPlafond(null)}
              className="bg-gray-400 text-white px-4 py-2 rounded"
            >
              Annuler
            </button>

          </div>
          {showOtpInput && (
    <div className="mt-4 p-4 bg-yellow-50 rounded">
        <h3>🔐 Vérification OTP</h3>

        <input
        placeholder="Entrer OTP"
        value={otpCode}
        onChange={(e) => setOtpCode(e.target.value)}
        className="border p-2 rounded w-full mb-2"
        />

        <button
        onClick={verifyOtp}
        className="bg-green-600 text-white px-4 py-2 rounded"
        >
        Confirmer
        </button>
    </div>
    )}

        </div>
      )}

      {/* ========================= MESSAGE ========================= */}
      {message && (
        <div className="rounded-2xl border border-blue-200 bg-gradient-to-r from-blue-50 to-indigo-50 px-5 py-4 shadow-sm">
          <div className="flex items-start gap-3">
            <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white grid place-items-center font-bold">
              i
            </div>
            <div className="text-sm text-slate-800">
              <div className="font-bold text-slate-900">Info</div>
              <div className="mt-0.5">{message}</div>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}