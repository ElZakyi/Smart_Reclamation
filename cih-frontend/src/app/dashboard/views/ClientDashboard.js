import api from "@/services/api";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function ClientDashboard({ user }) {
  const [message, setMessage] = useState("");
  const [reclamations, setReclamations] = useState([]);
  const [editIdReclamation,setEditIdReclamation] = useState(null);
  const [loadingAI, setLoadingAI] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [form, setForm] = useState({
    title: "",
    type: "AUTRE",
    canal: "AUTRE",
    priority: "LOW",
    description: "",
    isAiAssisted : false,
  });
  const router = useRouter();

  useEffect(() => {
  if (user?.idUser) {
    loadUserReclamation();
    }
    }, [user]);

  const handleChange = (e) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const createOrModifyReclamation = async (e) => {
    e.preventDefault();
    try {
      setLoadingSubmit(true);
      if(editIdReclamation){
        const res = await api.put(
          `/reclamations/${editIdReclamation}/user/${user.idUser}`,
          form
        );
        setMessage(res.data);
      } else {
        const res = await api.post(
          `/reclamations/user/${user.idUser}`,
          form
        );
        setMessage(res.data);
      }
      setForm({
        title:"",
        type:"AUTRE",
        canal:"AUTRE",
        priority:"LOW",
        description:"",
        isAiAssisted:false
      });
      setEditIdReclamation(null);
      loadUserReclamation();
    } catch(error){
      setMessage(error.response?.data?.error || "Erreur création");
    } finally {
      setLoadingSubmit(false);
    }
  };

  const loadUserReclamation = async () => {
    try {
      const res = await api.get(`/reclamations/user/${user.idUser}`);
      setReclamations(res.data);
    } catch (error) {
      setMessage(
        error.response?.data?.error ||
          "Erreur /GET récupérer reclamation par userId : " + error
      );
    }
  };
  const handleLogout = () => {
    localStorage.removeItem("token");
    router.push("/login");
  }
  const deleteReclamation = async(idReclamation) => {
    const ok = window.confirm("Supprimer cette reclamation ?");
    if(!ok) return;
    try{
        const res = await api.delete(`/reclamations/${idReclamation}/user/${user.idUser}`);
        setMessage(res.data);
        loadUserReclamation();
    }catch(error){
        setMessage(error.response?.data?.error || "Erreur /DELETE supprimer réclamation : " + error);
    }
  }
  const aiAssist = async () => {

    try {

      setLoadingAI(true);

      const res = await api.post("/classification/preview",{
        description:form.description
      });

      setForm(prev => ({
        ...prev,
        title : res.data.suggestedTitle,
        type : res.data.predictedType,
        canal : res.data.predictedCanal,
        priority : res.data.predictedPriority,
        isAiAssisted : true
      }));

    } catch(error){

      setMessage(error.response?.data?.error || "Erreur IA");

    } finally {

      setLoadingAI(false);

    }
  }
  // ✅ Helpers UI (pas logique métier)
  const statusBadge = (status) => {
  switch (status) {
    case "CREEE":
    case "CREE":
      return "bg-sky-100 text-sky-700 ring-sky-200";

    case "AFFECTEE":
      return "bg-indigo-100 text-indigo-700 ring-indigo-200";

    case "EN_COURS":
      return "bg-amber-100 text-amber-800 ring-amber-200";

    case "EN_ATTENTE_CLIENT":
      return "bg-yellow-100 text-yellow-800 ring-yellow-200";

    case "REJETEE":
      return "bg-rose-100 text-rose-700 ring-rose-200";

    case "RESOLUE":
  return "bg-emerald-100 text-emerald-700 ring-emerald-200";

    case "CLOTUREE":
  return "bg-green-200 text-green-900 ring-green-300"; // plus foncé

    default:
      return "bg-gray-100 text-gray-700 ring-gray-200";
  }
};
  const priorityBadge = (p) => {
    switch (p) {
      case "LOW":
        return "bg-emerald-50 text-emerald-700 ring-emerald-200";
      case "AVERAGE":
      case "MEDIUM":
        return "bg-yellow-100 text-amber-800 ring-amber-200";
      case "HIGH":
        return "bg-orange-100 text-orange-800 ring-orange-200";
      case "CRITICAL":
      case "URGENT":
        return "bg-rose-50 text-rose-700 ring-rose-200";
      default:
        return "bg-gray-50 text-gray-700 ring-gray-200";
    }
  };

  const canalBadge = (c) => {
    switch (c) {
      case "E_COMMERCE":
        return "bg-violet-50 text-violet-700 ring-violet-200";
      case "E_BANKING":
        return "bg-indigo-50 text-indigo-700 ring-indigo-200";
      case "GAB":
        return "bg-cyan-50 text-cyan-700 ring-cyan-200";
      case "CARTE":
        return "bg-teal-50 text-teal-700 ring-teal-200";
      default:
        return "bg-gray-50 text-gray-700 ring-gray-200";
    }
  };

  return (
    <div className="relative min-h-screen">

    {/* 🔥 BACKGROUND IMAGE */}
    <div
      className="fixed inset-0 -z-10 bg-cover bg-no-repeat"
      style={{
        backgroundImage: "url('/client_cih_4.png')",
      }}
    />

    {/* 🔥 OVERLAY (léger pour lisibilité) */}
    <div className="fixed inset-0 -z-10 bg-black/20 backdrop-blur-[10px]" />

    {/* CONTENU */}
    <div className="p-6 md:p-4">

      {/* LOGOUT */}
      <button
        onClick={handleLogout}
        className="flex ml-auto px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 
                  text-white font-semibold shadow-md transition active:scale-95"
      >
        Déconnexion
      </button>

      <div className="max-w-6xl mx-auto space-y-6">

  {/* HEADER */}
  <div className="relative flex flex-col md:flex-row items-center justify-center 
  rounded-2xl p-6 
  bg-gradient-to-r from-blue-600 via-orange-300 to-orange-500 shadow-lg">

    {/* 🔥 LOGO PLUS GRAND */}
    <img
      src="/Cih-bank.png"
      alt="CIH Bank"
      className="absolute left-6 top-1/2 -translate-y-1/2 w-33 h-33 object-contain"
    />

    {/* 🔥 TITRE CENTRÉ */}
    <div className="text-center">
      <h1 className="text-2xl md:text-3xl font-extrabold text-white">
        Tableau de bord Client
      </h1>
      <p className="text-sm text-white/80">
        Crée et suis tes réclamations en temps réel.
      </p>
    </div>

    {/* 🔥 USER CARD (reste à droite) */}
    <div className="absolute right-6 flex items-center gap-3 rounded-2xl 
    bg-white/20 backdrop-blur-md px-4 py-2 shadow-md">

      <div className="h-9 w-9 rounded-xl 
                      bg-white/30 text-white grid place-items-center font-bold">
        {(user?.fullName || user?.email || "U")
          .toString()
          .trim()
          .charAt(0)
          .toUpperCase()}
      </div>

      <div className="leading-tight text-left">
        <div className="text-sm font-semibold text-white">
          {user?.fullName ? user.fullName : "Client"}
        </div>
        <div className="text-xs text-white/80">
          {user?.email ? user.email : "—"}
        </div>
      </div>

    </div>

  </div>
          {/* Form Card */}
        <div className="relative overflow-hidden rounded-2xl 
                        border border-black/30 
                        bg-white/1 backdrop-blur-md 
                        shadow-xl">

          {/* TOP BAR */}
          <div className="absolute inset-x-0 top-0 h-1.5 
                          bg-gradient-to-r from-blue-600 via-indigo-600 to-violet-600" />

          {/* HEADER */}
          <div className="px-6 py-5 border-b border-white/30 
                          bg-white/30 backdrop-blur-md">

            <h2 className="text-lg font-bold text-gray-900 flex ml-110">
              Créer une réclamation
            </h2>

            <p className="text-sm text-gray-700 flex ml-105">
              Remplis les infos, puis clique sur “Créer”.
            </p>

            {/* ACTIONS */}
            <div className="flex items-center justify-between mt-3">

              <button
                onClick={() => router.push("/dashboard/plafond")}
                className="px-4 py-2 text-sm 
                          bg-gradient-to-r from-indigo-600 to-blue-600 
                          text-white rounded-lg shadow-md 
                          hover:opacity-90 transition"
              >
                Demande plafond
              </button>

              <button
                disabled={!form.description.trim() || loadingAI}
                onClick={aiAssist}
                className="px-4 py-2 text-sm 
                          bg-orange-400/90 text-white rounded-lg 
                          disabled:bg-gray-400 
                          hover:bg-orange-700 transition shadow-md"
              >
                {loadingAI ? "Analyse..." : "Assisté IA"}
              </button>

            </div>

          </div>
            <div className="p-6">
              <form onSubmit={createOrModifyReclamation} className="space-y-5">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-1">
                      Titre
                    </label>
                    <input
                      type="text"
                      placeholder="Ex: Paiement refusé"
                      name="title"
                      value={form.title}
                      onChange={handleChange}
                      className="w-full rounded-xl border border-slate-300 bg-white/50 px-4 py-2.5 text-slate-900 shadow-sm
                                 focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                    />
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 md:col-span-2">
                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        Type
                      </label>
                      <select
                        name="type"
                        value={form.type}
                        onChange={handleChange}
                        className="w-full rounded-xl border border-slate-300 bg-white/50 px-4 py-2.5 text-slate-900 shadow-sm
                                   focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                      >
                        <option value="AUTRE">AUTRE</option>
                        <option value="RETRAIT_GAB">RETRAIT_GAB</option>
                        <option value="PROBLEME_ECOMMERCE">PROBLEME_ECOMMERCE</option>
                        <option value="PAIEMENT_REFUSE">PAIEMENT_REFUSE</option>
                        <option value="FRAUDE_SUSPECTEE">FRAUDE_SUSPECTEE</option>
                        <option value="ERREUR_FACTURATION">ERREUR_FACTURATION</option>
                        <option value="DOUBLE_DEBIT">DOUBLE_DEBIT</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        Canal
                      </label>
                      <select
                        name="canal"
                        value={form.canal}
                        onChange={handleChange}
                        className="w-full rounded-xl border border-slate-300 bg-white/50 px-4 py-2.5 text-slate-900 shadow-sm
                                   focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                      >
                        <option value="AUTRE">AUTRE</option>
                        <option value="CARTE">CARTE</option>
                        <option value="E_COMMERCE">E_COMMERCE</option>
                        <option value="E_BANKING">E_BANKING</option>
                        <option value="GAB">GAB</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-slate-700 mb-1">
                        Priorité
                      </label>
                      <select
                        name="priority"
                        value={form.priority}
                        onChange={handleChange}
                        className="w-full rounded-xl border border-slate-300 bg-white/50 px-4 py-2.5 text-slate-900 shadow-sm
                                   focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                      >
                        <option value="LOW">FAIBLE</option>
                        <option value="AVERAGE">MOYENNE</option>
                        <option value="HIGH">ELEVÉE</option>
                        <option value="CRITICAL">CRITIQUE</option>
                      </select>
                    </div>
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-semibold text-slate-700 mb-1">
                    Description
                  </label>
                  <textarea
                    name="description"
                    placeholder="Décris ton problème en détail..."
                    value={form.description}
                    onChange={handleChange}
                    required
                    rows={4}
                    className="w-full rounded-xl border border-slate-300 bg-white/50 px-4 py-2.5 text-slate-900 shadow-sm
                               focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                  />
                  {loadingAI && (
                    <div className="text-sm text-indigo-600 mt-2 flex items-center gap-2">
                      <div className="animate-spin h-4 w-4 border-2 border-indigo-600 border-t-transparent rounded-full"></div>
                      L'IA analyse la description...
                    </div>
                  )}
                  {loadingSubmit && (
                    <div className="flex items-center gap-2 text-blue-600 text-sm mt-2">
                      <div className="animate-spin h-4 w-4 border-2 border-blue-600 border-t-transparent rounded-full"></div>
                      Création de la réclamation...
                    </div>
                  )}
                </div>

                <div className="flex flex-wrap gap-3">
                  <button
                    type="submit"
                    disabled={loadingSubmit}
                    className="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600
                              px-5 py-2.5 text-white font-semibold shadow-md hover:brightness-110
                              disabled:bg-gray-400 transition"
                  >
                    {loadingSubmit ? "Création en cours..." : (editIdReclamation ? "Modifier" : "Créer")}
                  </button>
                  {editIdReclamation && <button 
                   className="inline-flex items-center justify-center rounded-xl bg-slate-100 px-5 py-2.5
                               text-slate-800 font-semibold shadow-sm hover:bg-slate-200 active:scale-[0.99] transition"
                  onClick={()=>{
                    setForm({
                        title: "",
                        type: "AUTRE",
                        canal: "AUTRE",
                        priority: "LOW",
                        description: "",
                        isAiAssisted: false,
                      });
                    setEditIdReclamation(null);}}
                  >
                    Annuler
                    </button>}

                  <button
                    type="button"
                    onClick={() =>
                      setForm({
                        title: "",
                        type: "AUTRE",
                        canal: "AUTRE",
                        priority: "LOW",
                        description: "",
                        isAiAssisted: false,
                      })
                    }
                    className="inline-flex items-center justify-center rounded-xl bg-orange-400 px-5 py-2.5
                               text-slate-100 font-semibold shadow-sm hover:bg-slate-200 active:scale-[0.99] transition"
                  >
                    Réinitialiser
                  </button>
                </div>
              </form>
            </div>
          </div>

          {/* List */}
          <div className="rounded-2xl border border-white/30 bg-white/10 backdrop-blur-md shadow-lg overflow-hidden">
            <div className="px-6 py-4 border-b border-white/30 bg-white/20 backdrop-blur-md flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-slate-900 flex ml-115">Mes réclamations</h2>
                <p className="text-sm text-slate-600">
                  Historique et état actuel de tes demandes.
                </p>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-xs font-semibold text-slate-600">Total</span>
                <span className="rounded-full bg-slate-100 px-3 py-1 text-sm font-bold text-slate-900">
                  {reclamations.length}
                </span>
              </div>
            </div>

            <div>
              {/* TABLE RECLAMATIONS */}
<div className="overflow-x-auto rounded-2xl backdrop-blur-sm bg-white/10 border border-white/30 shadow-lg">

  <table className="w-full divide-y divide-white/20 bg-transparent">

    {/* HEADER */}
    <thead className="bg-white/50 backdrop-blur-md">
      <tr className="text-sm text-slate-800"> 
        <th className="px-4 py-4 text-left font-semibold w-[130px] border-r border-white/20">Référence</th>
        <th className="px-4 py-4 text-left font-semibold w-[200px] border-r border-white/20">Titre</th>
        <th className="px-4 py-4 text-left font-semibold w-[120px] border-r border-white/20">Statut</th>
        <th className="px-4 py-4 text-left font-semibold w-[110px] border-r border-white/20">Priorité</th>
        <th className="px-4 py-4 text-left font-semibold w-[110px] border-r border-white/20">Canal</th>
        <th className="px-4 py-4 text-left font-semibold w-[160px] border-r border-white/20">Créée le</th>
        <th className="px-4 py-4 text-left font-semibold w-[200px] border-r border-white/20">Actions</th>
      </tr>
    </thead>

    {/* BODY */}
    <tbody className="divide-y divide-white/20">

      {reclamations.map((reclamation) => (
        <tr
          key={reclamation.idReclamation}
          className="hover:bg-white/30 transition"
        >

          {/* REFERENCE */}
          <td className="px-4 py-4 text-sm font-semibold text-slate-900 break-words border-r border-white/20">
            {reclamation.reference}
          </td>

          {/* TITRE */}
          <td className="px-4 py-4 text-sm text-slate-900 border-r border-white/20">
            <div className="font-semibold break-words">
              {reclamation.title}
            </div>
            <div className="text-xs text-slate-500">
              {reclamation.type || "—"}
            </div>
          </td>

          {/* STATUS */}
          <td className="px-4 py-4 border-r border-white/20">
            <span
              className={`inline-flex items-center px-3 py-1.5 rounded-full text-xs font-semibold ${statusBadge(
                reclamation.status
              )}`}
            >
              {reclamation.status}
            </span>
          </td>

          {/* PRIORITE */}
          <td className="px-4 py-4 border-r border-white/20">
            <span
              className={`inline-flex items-center px-3 py-1.5 rounded-full text-xs font-semibold ${priorityBadge(
                reclamation.priority
              )}`}
            >
              {reclamation.priority}
            </span>
          </td>

          {/* CANAL */}
          <td className="px-4 py-4 border-r border-white/20">
            <span
              className={`inline-flex items-center px-3 py-1.5 rounded-full text-xs font-semibold ${canalBadge(
                reclamation.canal
              )}`}
            >
              {reclamation.canal}
            </span>
          </td>

          {/* DATE */}
          <td className="px-4 py-4 text-sm text-slate-700 border-r border-white/20">
            {reclamation.createdAt
              ? new Date(reclamation.createdAt).toLocaleString()
              : "-"}
          </td>

          {/* ACTIONS */}
          <td className="px-4 py-4 ">
            <div className="flex flex-wrap gap-2">

              {/* DELETE */}
              <button
                onClick={() => deleteReclamation(reclamation.idReclamation)}
                className="bg-red-500 hover:bg-red-600 text-white px-3 py-1.5 text-xs rounded-lg shadow transition"
              >
                Supprimer
              </button>

              {/* EDIT */}
              <button
                onClick={() => {
                  setForm({
                    title: reclamation.title,
                    type: reclamation.type,
                    canal: reclamation.canal,
                    priority: reclamation.priority,
                    description: reclamation.description,
                    isAiAssisted:false
                  });
                  setEditIdReclamation(reclamation.idReclamation);
                }}
                className="bg-orange-500 hover:bg-orange-600 text-white px-3 py-1.5 text-xs rounded-lg shadow transition"
              >
                Modifier
              </button>

              {/* DETAILS */}
              <button
                onClick={() => {
                  router.push(`/dashboard/reclamation/${reclamation.idReclamation}`);
                }}
                className="text-indigo-600 hover:underline text-xs font-semibold"
              >
                Voir détails
              </button>

            </div>
          </td>

        </tr>
      ))}

      {/* EMPTY */}
      {reclamations.length === 0 && (
        <tr>
          <td
            className="px-6 py-10 text-sm text-slate-500 italic text-center"
            colSpan={7}
          >
            Aucune réclamation pour le moment.
          </td>
        </tr>
      )}

    </tbody>
  </table>
</div>
            </div>
          </div>
          {/* Message */}
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
    </div>
  );
}