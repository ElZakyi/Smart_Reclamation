import api from "@/services/api";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function ClientDashboard({ user }) {
  const [message, setMessage] = useState("");
  const [reclamations, setReclamations] = useState([]);
  const [editIdReclamation,setEditIdReclamation] = useState(null);
  const [form, setForm] = useState({
    title: "",
    type: "AUTRE",
    canal: "AUTRE",
    priority: "LOW",
    description: "",
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
    if(editIdReclamation){
        try {
            const res = await api.put(`/reclamations/${editIdReclamation}/user/${user.idUser}`,form);
            setMessage(res.data);
            setForm({
            title : "",
            type : "AUTRE",
            canal : "AUTRE",
            priority : "LOW",
            description : ""
            });
            setEditIdReclamation(null);
            loadUserReclamation();
        }catch(error){
            setMessage(error.response?.data?.error || "Error /PUT modify reclamation");
        }
    }
    
    else {
        try {
      const res = await api.post(`/reclamations/user/${user.idUser}`, form);
      setMessage(res.data);
      setForm({
        title: "",
        type: "AUTRE",
        canal: "AUTRE",
        priority: "LOW",
        description: "",
      });
      loadUserReclamation();
    } catch (error) {
      setMessage(error.response?.data?.error || "Error /POST create réclamation :" + error);
    }
    }
  };

  const loadUserReclamation = async () => {
    try {
      const res = await api.get(`/reclamations/user/${user.idUser}`);
      setReclamations(res.data);
    } catch (error) {
      setMessage(
        error.response?.data?.error ||
          "Error /GET get reclamation by userId : " + error
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
        setMessage(error.response?.data?.error || "Error /DELETE delete reclamation : " + error);
    }
  }
  // ✅ Helpers UI (pas logique métier)
  const statusBadge = (status) => {
    switch (status) {
      case "CREEE":
      case "CREE":
        return "bg-sky-100 text-sky-700 ring-sky-200";
      case "EN_COURS":
        return "bg-amber-100 text-amber-800 ring-amber-200";
      case "RESOLUE":
        return "bg-emerald-100 text-emerald-700 ring-emerald-200";
      case "REJETEE":
        return "bg-rose-100 text-rose-700 ring-rose-200";
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
        return "bg-amber-50 text-amber-800 ring-amber-200";
      case "HIGH":
        return "bg-orange-50 text-orange-800 ring-orange-200";
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
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      {/* Top accent bar */}
      <div />

      <div className="p-6 md:p-1">
        <button
            onClick={handleLogout}
            className="flex ml-auto px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 
                      text-white font-semibold shadow-md transition 
                      active:scale-95"
          >
            Déconnexion
          </button>
        <div className="max-w-6xl mx-auto space-y-6">
          {/* Header */}
          <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <div>
              <h1 className="text-2xl md:text-3xl font-extrabold text-slate-900">
                Client Dashboard
              </h1>
              <p className="text-sm text-slate-600">
                Crée et suis tes réclamations en temps réel.
              </p>
            </div>

            <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-white/70 backdrop-blur px-4 py-2 shadow-sm">
              <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white grid place-items-center font-bold">
                {(user?.fullName || user?.email || "U").toString().trim().charAt(0).toUpperCase()}
              </div>
              <div className="leading-tight">
                <div className="text-sm font-semibold text-slate-900">
                  {user?.fullName ? user.fullName : "Utilisateur"}
                </div>
                <div className="text-xs text-slate-500">
                  {user?.email ? user.email : "—"}
                </div>
              </div>
            </div>
          </div>

          {/* Form Card */}
          <div className="relative overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-lg">
            <div className="absolute inset-x-0 top-0 h-1.5 bg-gradient-to-r from-blue-600 via-indigo-600 to-violet-600" />
            <div className="px-6 py-5 border-b border-slate-200 bg-gradient-to-r from-white to-slate-50">
              <h2 className="text-lg font-bold text-slate-900">
                Créer une réclamation
              </h2>
              <p className="text-sm text-slate-600">
                Remplis les infos, puis clique sur “Créer”.
              </p>
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
                      className="w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-slate-900 shadow-sm
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
                        className="w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-slate-900 shadow-sm
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
                        className="w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-slate-900 shadow-sm
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
                        className="w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-slate-900 shadow-sm
                                   focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                      >
                        <option value="LOW">LOW</option>
                        <option value="AVERAGE">AVERAGE</option>
                        <option value="HIGH">HIGH</option>
                        <option value="CRITICAL">CRITICAL</option>
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
                    className="w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-slate-900 shadow-sm
                               focus:outline-none focus:ring-4 focus:ring-blue-200 focus:border-blue-500"
                  />
                </div>

                <div className="flex flex-wrap gap-3">
                  <button
                    type="submit"
                    className="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600
                               px-5 py-2.5 text-white font-semibold shadow-md hover:brightness-110 active:scale-[0.99] transition"
                  >
                    {editIdReclamation ? "Modifier" : "Créer" }
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      setForm({
                        title: "",
                        type: "AUTRE",
                        canal: "AUTRE",
                        priority: "LOW",
                        description: "",
                      })
                    }
                    className="inline-flex items-center justify-center rounded-xl bg-slate-100 px-5 py-2.5
                               text-slate-800 font-semibold shadow-sm hover:bg-slate-200 active:scale-[0.99] transition"
                  >
                    Réinitialiser
                  </button>
                </div>
              </form>
            </div>
          </div>

          {/* List */}
          <div className="rounded-2xl border border-slate-200 bg-white shadow-lg overflow-hidden">
            <div className="px-6 py-4 border-b border-slate-200 bg-gradient-to-r from-white to-slate-50 flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-slate-900">Mes réclamations</h2>
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

            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-slate-200">
                <thead className="bg-slate-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                      Référence
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                      Titre
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                      Priorité
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                      Canal
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                      Créée le
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-bold text-slate-600 uppercase tracking-wider">
                        Actions
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-slate-200 bg-white">
                  {reclamations.map((reclamation) => (
                    <tr
                      key={reclamation.idReclamation}
                      className="hover:bg-indigo-50/40 transition"
                    >
                      <td className="px-6 py-4 text-sm font-semibold text-slate-800">
                        {reclamation.reference}
                      </td>

                      <td className="px-6 py-4 text-sm text-slate-900">
                        <div className="font-semibold">{reclamation.title}</div>
                        <div className="text-xs text-slate-500">
                          {reclamation.type || "—"}
                        </div>
                      </td>

                      <td className="px-6 py-4 text-sm">
                        <span
                          className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-bold ring-1 ${statusBadge(
                            reclamation.status
                          )}`}
                        >
                          {reclamation.status}
                        </span>
                      </td>

                      <td className="px-6 py-4 text-sm">
                        <span
                          className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-bold ring-1 ${priorityBadge(
                            reclamation.priority
                          )}`}
                        >
                          {reclamation.priority}
                        </span>
                      </td>

                      <td className="px-6 py-4 text-sm">
                        <span
                          className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-bold ring-1 ${canalBadge(
                            reclamation.canal
                          )}`}
                        >
                          {reclamation.canal}
                        </span>
                      </td>

                      <td className="px-6 py-4 text-sm text-slate-700">
                        {reclamation.createdAt
                          ? new Date(reclamation.createdAt).toLocaleString()
                          : "-"}
                      </td>
                      <td className="px-6 py-4 text-sm">
                    <div className="flex gap-2">

                        <button
                        onClick={() => deleteReclamation(reclamation.idReclamation)}
                        className="inline-flex items-center justify-center
                                    rounded-lg bg-red-500 hover:bg-red-600
                                    px-3 py-1.5 text-xs font-semibold text-white
                                    shadow-sm transition active:scale-95"
                        >
                        Supprimer
                        </button>

                        <button
                        onClick={() => {
                            setForm({
                            title: reclamation.title,
                            type: reclamation.type,
                            canal: reclamation.canal,
                            priority: reclamation.priority,
                            description: reclamation.description
                            });
                            setEditIdReclamation(reclamation.idReclamation);
                        }}
                        className="inline-flex items-center justify-center
                                    rounded-lg bg-amber-500 hover:bg-amber-600
                                    px-3 py-1.5 text-xs font-semibold text-white
                                    shadow-sm transition active:scale-95"
                        >
                        Modifier
                        </button>

                    </div>
                    </td>
                    </tr>
                  ))}   

                  {reclamations.length === 0 && (
                    <tr>
                      <td
                        className="px-6 py-10 text-sm text-slate-500 italic"
                        colSpan={6}
                      >
                        Aucune réclamation pour le moment.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
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