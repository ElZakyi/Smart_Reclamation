"use client";

import { useEffect, useState } from "react";
import api from "@/services/api";
import { useRouter } from "next/navigation";

export default function PlafondPage() {
  const router = useRouter();
  const [cardForm, setCardForm] = useState({
  cardNumberMasked: "",
  cardType: "",
  currentLimit: "",
  cvc: "",
  expiryDate: ""
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
      const payload = {
          ...cardForm,
          expiryDate: cardForm.expiryDate + "-01" // 🔥 FIX
        };
      if (editingCardId) {
        await api.put(`/card/${editingCardId}`, payload);
        setMessage("Carte modifiée avec succès !");
      } else {
        await api.post(`/card/${currentUser.idUser}`, payload);
        setMessage("Carte créée avec succès !");
      }

      setCardForm({
        cardNumberMasked: "",
        cardType: "",
        currentLimit: "",
        cvc: "",
        expiryDate:"",
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
      currentLimit: card.currentLimit,
      cvc : card.cvc,
      expiryDate: card.expiryDate
      ? card.expiryDate.substring(0, 7) // 🔥 FIX ICI
      : ""
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
    setMessage(" Code éxpiré ou incorrect");
  }
};
const maskCardNumber = (num) => {
  if (!num) return "";
  const last4 = num.slice(-4);
  return "**** **** **** " + last4;
};
const resetForm = () => {
  setCardForm({
    cardNumberMasked: "",
    cardType: "",
    currentLimit: "",
    cvc: "",
    expiryDate: ""
  });

  setEditingCardId(null);
};
const handleLogout = () => {
      localStorage.removeItem("token");
      router.push("/login");
    }

  return (
  <div
    className="min-h-screen bg-cover bg-center bg-fixed  relative"
    style={{
      backgroundImage: "url('/plafond.png')" // ⚠️ mets ton image ici
    }}
  >

    {/* OVERLAY GLASS */}
    <div className="absolute inset-0 bg-black/30 backdrop-blur-[5px]"></div>
    
    {/* CONTENU */}
    <div className="relative z-10 max-w-6xl mx-auto p-6 space-y-8">
    <button
  onClick={handleLogout}
  className="fixed top-5 right-6 z-50 px-4 py-2 rounded-xl 
  bg-red-500 hover:bg-red-600 text-white font-semibold 
  shadow-lg transition active:scale-95"
>
  Déconnexion
</button>

      {/* HEADER */}
      <div className="relative rounded-2xl p-6 text-white 
      bg-gradient-to-r from-blue-600 via-orange-300 to-orange-500 shadow-lg overflow-hidden">

        <img
          src="/Cih-bank.png"
          alt="CIH Bank"
          className="absolute left-6 top-1/2 -translate-y-1/2 w-36 h-36 object-contain opacity-90"
        />

        <div className="text-center">
          <h1 className="text-3xl font-bold">Gestion des cartes</h1>
          <p className="text-sm opacity-90 mt-1">
            Gérez vos cartes bancaires
          </p>
        </div>
      </div>

      {/* ================= FORM ================= */}
      <form
        onSubmit={handleSubmit}
        className="bg-white/30 backdrop-blur-xl border border-white/40 
        rounded-2xl p-6 shadow-lg space-y-6"
      >

        <h2 className="text-xl font-bold">
          ➕ Ajouter une nouvelle carte
        </h2>

        <div className="grid md:grid-cols-3 gap-4">

          <input
            placeholder="Numéro de carte"
            value={cardForm.cardNumberMasked || ""}
            onChange={(e) =>
              setCardForm({
                ...cardForm,
                cardNumberMasked: e.target.value
              })
            }
            className="bg-white/70 border border-gray-300 rounded-xl p-3"
          />

          <select
            value={cardForm.cardType || ""}
            onChange={(e) =>
              setCardForm({
                ...cardForm,
                cardType: e.target.value
              })
            }
            className="bg-white/70 border border-gray-300 rounded-xl p-3"
          >
            <option value="">Choisir votre type de carte</option>
            <option value="MASTERCARD">Mastercard</option>
            <option value="VISA">Visa</option>
            <option value="SAYIDATI">Sayidati</option>
          </select>

          <input
            placeholder="CVC"
            maxLength={3}
            value={cardForm.cvc || ""}
            onChange={(e) =>
              setCardForm({
                ...cardForm,
                cvc: e.target.value
              })
            }
            className="bg-white/70 border border-gray-300 rounded-xl p-3"
          />

          <input
            type="month"
            value={cardForm.expiryDate || ""}
            onChange={(e) =>
              setCardForm({
                ...cardForm,
                expiryDate: e.target.value
              })
            }
            className="bg-white/70 border border-gray-300 rounded-xl p-3"
          />

          <input
            placeholder="Limite actuelle"
            type="number"
            value={cardForm.currentLimit || ""}
            onChange={(e) =>
              setCardForm({
                ...cardForm,
                currentLimit: e.target.value
              })
            }
            className="bg-white/70 border border-gray-300 rounded-xl p-3"
          />

        </div>

        <div className="flex gap-3 flex-wrap">

        {/* SUBMIT */}
        <button
          type="submit"
          className={`flex-1 py-3 rounded-xl text-white ${
            editingCardId
              ? "bg-orange-600 hover:bg-orange-700"
              : "bg-blue-600 hover:bg-blue-700"
          }`}
        >
          {editingCardId ? "Modifier carte" : "Créer carte"}
        </button>

        {/* ANNULER (UNIQUEMENT EN MODE EDIT) */}
        {editingCardId && (
          <button
            type="button"
            onClick={resetForm}
            className="flex-1 py-3 rounded-xl bg-gray-400 hover:bg-gray-500 text-white transition"
          >
            Annuler
          </button>
        )}

      </div>

      </form>

      {/* ================= TABLE ================= */}
      <div className="rounded-2xl overflow-hidden shadow-lg">

        <div className="flex justify-between items-center px-6 py-4 bg-orange-500 text-white font-bold">
          <h2 className="text-xl">Mes cartes</h2>
          <span className="bg-white/30 px-4 py-1 rounded-xl text-sm">
            {cards.length} cartes
          </span>
        </div>

        <div className="bg-white/10 backdrop-blur-xl p-4 overflow-x-auto">

          <table className="w-full border-collapse">

            <thead>
              <tr className="bg-white/60 text-gray-700">
                <th className="p-3">Numéro</th>
                <th className="p-3">Type</th>
                <th className="p-3">Limite</th>
                <th className="p-3">Statut</th>
                <th className="p-3 text-center">Actions</th>
              </tr>
            </thead>

            <tbody>

              {cards.map((card) => (
                <tr key={card.idCard} className="border-b bg-white/30 hover:bg-white/90">

                  <td className="p-3 font-semibold">
                    **** **** **** {card.cardNumberMasked.slice(-4)}
                  </td>

                  <td className="p-3">
                    <span className="bg-red-500 text-white px-3 py-1 rounded-xl text-sm">
                      {card.cardType}
                    </span>
                  </td>

                  <td className="p-3 font-semibold text-green-700">
                    {card.currentLimit} DH
                  </td>

                  <td className="p-3">
                    <span className={`px-3 py-1 rounded-xl text-sm ${
                      card.status === "ACTIVE"
                        ? "bg-green-100 text-green-700"
                        : "bg-red-100 text-red-700"
                    }`}>
                      {card.status}
                    </span>
                  </td>

                  <td className="p-3">
                    <div className="flex gap-2 justify-center flex-wrap">

                      <button
                        onClick={() => editCard(card)}
                        className="bg-orange-500 text-white px-3 py-2 rounded-lg"
                      >
                        Modifier
                      </button>

                      <button
                        onClick={() => deleteCard(card.idCard)}
                        className="bg-red-600 text-white px-3 py-2 rounded-lg"
                      >
                        Supprimer
                      </button>

                      <button
                        onClick={() => setSelectedCardForPlafond(card)}
                        className="bg-indigo-600 text-white px-3 py-2 rounded-lg"
                      >
                        Plafond
                      </button>

                    </div>
                  </td>

                </tr>
              ))}

            </tbody>

          </table>

        </div>
      </div>

      {/* ================= PLAFOND ================= */}
      {selectedCardForPlafond && (
  <div className="rounded-2xl overflow-hidden shadow-lg">

    {/* HEADER */}
    <div className="px-6 py-4 bg-blue-600 text-white font-bold text-center">
      💳 Changement de plafond
    </div>

    {/* BODY */}
    <div className="bg-white/30 backdrop-blur-xl p-6 space-y-5">

      {/* CARTE */}
      <div className="bg-white/60 border border-gray-200 rounded-xl p-4 text-center">
        <p className="text-sm text-gray-500 mb-1">
          Numéro de carte
        </p>
        <p className="font-semibold text-slate-900 tracking-wide">
          **** **** **** {selectedCardForPlafond.cardNumberMasked.slice(-4)}
        </p>
      </div>

      {/* INPUT PLAFOND */}
      <div>
        <label className="text-sm font-semibold text-gray-600">
          Nouveau plafond
        </label>

        <input
          name="newPlafond"
          value={plafondForm.newPlafond || ""}
          onChange={handlePlafondChange}
          placeholder="Ex: 5000 DH"
          className="w-full mt-1 p-3 rounded-xl bg-white/70 border border-gray-300 
          focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
      </div>

      {/* JUSTIFICATION */}
      <div>
        <label className="text-sm font-semibold text-gray-600">
          Justification
        </label>

        <textarea
          name="justification"
          value={plafondForm.justification || ""}
          onChange={handlePlafondChange}
          placeholder="Expliquez la raison..."
          className="w-full mt-1 p-3 rounded-xl bg-white/70 border border-gray-300 
          focus:outline-none focus:ring-2 focus:ring-blue-400"
        />
      </div>

      {/* BOUTONS */}
      <div className="flex gap-3 flex-wrap">

        <button
          onClick={submitPlafondRequest}
          className="flex-1 bg-blue-600 hover:bg-blue-700 text-white 
          px-4 py-3 rounded-xl shadow transition"
        >
          Envoyer
        </button>

        <button
          onClick={() => setSelectedCardForPlafond(null)}
          className="flex-1 bg-gray-400 hover:bg-gray-500 text-white 
          px-4 py-3 rounded-xl transition"
        >
          Annuler
        </button>

      </div>

      {/* OTP */}
      {showOtpInput && (
        <div className="mt-4 bg-yellow-100/70 border border-yellow-300 rounded-xl p-4 space-y-3">

          <p className="text-sm font-semibold text-yellow-800 text-center">
            🔐 Vérification OTP
          </p>

          <input
            value={otpCode || ""}
            onChange={(e) => setOtpCode(e.target.value)}
            placeholder="Entrer le code OTP"
            className="w-full p-2 rounded-lg border border-yellow-300 
            focus:outline-none focus:ring-2 focus:ring-yellow-400"
          />

          <button
            onClick={verifyOtp}
            className="w-full bg-green-600 hover:bg-green-700 text-white 
            py-2 rounded-xl transition"
          >
            Confirmer
          </button>

        </div>
      )}
      

    </div>
    {/* ========================= MESSAGE ========================= */}
      
  </div>
)}
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
    
    </div>
    
  );
}