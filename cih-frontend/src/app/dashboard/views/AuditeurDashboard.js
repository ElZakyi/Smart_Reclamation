"use client"

import { useEffect, useState } from "react";
import api from "@/services/api";
import { useRouter } from "next/navigation";

export default function AuditeurDashboard(){

    const [logs,setLogs] = useState([]);
    const [filteredLogs,setFilteredLogs] = useState([]);
    const [loading,setLoading] = useState(true);

    const [search,setSearch] = useState("");
    const [actionFilter,setActionFilter] = useState("");
    const [entityFilter,setEntityFilter] = useState("");
    const [entitySort, setEntitySort] = useState(""); // "", "asc", "desc
    const router = useRouter();

    useEffect(()=>{
        loadAudit();
    },[]);

    useEffect(()=>{
        filterLogs();
    },[logs,search,actionFilter,entityFilter,entitySort]);

    const loadAudit = async () => {
        try{
            const res = await api.get("/audit");
            setLogs(res.data);
        }catch(e){
            console.error("Erreur audit",e);
        }finally{
            setLoading(false);
        }
    }
    const entityBadge = (entity) => {
  const e = entity?.toLowerCase()?.trim();

  switch (e) {

    // 👤 USERS / IDENTITÉ
    case "utilisateur":
    case "user":
      return "bg-indigo-100 text-indigo-800";

    case "assignment":
    case "assignement_réclamation":
      return "bg-violet-100 text-violet-800";


    // 👥 ORGANISATION
    case "équipe":
    case "equipe":
    case "team":
      return "bg-purple-100 text-purple-800";


    // 📢 MÉTIER PRINCIPAL (réclamations)
    case "réclamation":
    case "reclamation":
      return "bg-blue-100 text-blue-800";


    // ⚙️ TRAITEMENT / WORKFLOW
    case "routing_suggestion":
      return "bg-sky-100 text-sky-800";

    case "résolution":
    case "resolution":
      return "bg-emerald-100 text-emerald-800";


    // 📊 DÉCISIONS
    case "proposition_décision":
    case "proposition_decision":
    case "decisionproposal":
      return "bg-amber-100 text-amber-800";

    case "decision":
    case "décision":
      return "bg-pink-100 text-pink-800";

    case "décision_plafond":
    case "decision_plafond":
      return "bg-fuchsia-100 text-fuchsia-800";


    // 💳 PLAFOND / FINANCE
    case "plafondrequest":
    case "plafond_request":
    case "demande_plafond":
      return "bg-rose-100 text-rose-800";


    // 🔔 SUPPORT
    case "notification":
      return "bg-yellow-100 text-yellow-800";

    case "attachment":
    case "pièce jointe":
    case "piece jointe":
      return "bg-teal-100 text-teal-800";


    // ❓ DEFAULT
    default:
      return "bg-gray-100 text-gray-700";
  }
};

    const actionLabels = {
        CREATE_RECLAMATION: "Création de réclamation",
        UPDATE_RECLAMATION: "Modification de réclamation",
        DELETE_RECLAMATION: "Suppression de réclamation",

        AI_CLASSIFICATION: "Analyse IA",
        AI_ROUTING_SUGGESTED: "Suggestion de routage IA",

        ASSIGN: "Affectation",
        REASSIGN: "Réaffectation",
        MANUAL_ASSIGNMENT: "Affectation manuelle",
        ACCEPT_ROUTING: "Acceptation du routage",
        REJECT_ROUTING: "Rejet du routage",

        SEND_MESSAGE: "Envoi de message",

        UPLOAD_ATTACHMENT: "Ajout de pièce jointe",
        DELETE_ATTACHMENT: "Suppression de pièce jointe",

        CREATE_RESOLUTION: "Création de résolution",

        PROPOSE_DECISION: "Proposition de décision",
        DECIDE_REJET: "Décision de rejet",
        DECIDE_CLOTURE: "Décision de clôture",

        WORKFLOW_ALLOWED: "Transition autorisée",
        WORKFLOW_DENIED: "Transition refusée",

        CREATE_USER: "Création utilisateur",
        UPDATE_USER: "Modification utilisateur",
        ACTIVATE_USER: "Activation utilisateur",
        DEACTIVATE_USER: "Désactivation utilisateur",

        ASSIGN_ROLE: "Attribution de rôle",
        REMOVE_ROLE: "Retrait de rôle",
        UPDATE_ROLE: "Modification de rôle",

        CREATE_TEAM: "Création équipe",
        UPDATE_TEAM: "Modification équipe",
        ACTIVATE_TEAM: "Activation équipe",
        DEACTIVATE_TEAM: "Désactivation équipe",
        ASSIGN_USER_TO_TEAM: "Ajout utilisateur à équipe",
        REMOVE_USER_FROM_TEAM: "Retrait utilisateur d’équipe",

        SEND_NOTIFICATION: "Envoi notification",

        CREATE_PLAFOND_REQUEST: "Demande de plafond",
        PROPOSE_PLAFOND_CHANGE: "Proposition de plafond",
        VALIDATE_PLAFOND_CHANGE: "Validation plafond",
        REFUSE_PLAFOND_CHANGE: "Refus plafond",

        CREATE_PLAFOND_PROPOSAL : "Proposition de plafond ",
        VERIFY_OTP : "Vérifier OTP",
        GENERATE_OTP : "Générer OTP"
    };

    const filterLogs = () => {
    let filtered = logs;

    // 🔍 SEARCH
    if (search) {
        const searchLower = search.toLowerCase();

        filtered = filtered.filter(log => {
            const actionRaw = log.action?.toLowerCase() || "";
            const actionFr = actionLabels[log.action]?.toLowerCase() || "";
            const entity = log.entityType?.toLowerCase() || "";
            const user = log.user?.fullName?.toLowerCase() || "";

            return (
                actionRaw.includes(searchLower) ||
                actionFr.includes(searchLower) ||
                entity.includes(searchLower) ||
                user.includes(searchLower)
            );
        });
    }

    // 🎯 FILTER ACTION
    if (actionFilter) {
        filtered = filtered.filter(log => log.action === actionFilter);
    }

    // 🎯 FILTER ENTITY
    if (entityFilter) {
        filtered = filtered.filter(log => log.entityType === entityFilter);
    }

    // 🔥 TRI PAR ENTITY (ICI CORRECT)
    if (entitySort) {
        filtered = filtered.sort((a, b) => {
            const entityA = a.entityType?.toLowerCase() || "";
            const entityB = b.entityType?.toLowerCase() || "";

            if (entitySort === "asc") {
                return entityA.localeCompare(entityB);
            }

            if (entitySort === "desc") {
                return entityB.localeCompare(entityA);
            }

            return 0;
        });
    }

    setFilteredLogs(filtered);
};

    const getBadgeColor = (action) => {
        if(action?.includes("CREATE")) return "bg-green-100 text-green-700";
        if(action?.includes("DELETE")) return "bg-red-100 text-red-700";
        if(action?.includes("UPDATE")) return "bg-yellow-100 text-yellow-700";
        if(action?.includes("DECIDE")) return "bg-purple-100 text-purple-700";
        return "bg-blue-100 text-blue-700";
    }

    if(loading) return <p className="p-6">Loading...</p>;
    const handleLogout = () => {
        localStorage.removeItem("token");
        router.push("/login");
    };

    return (
    <div className="relative min-h-screen p-6">
        <button
                    onClick={handleLogout}
                    className="px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 text-white font-semibold shadow-md transition active:scale-95 flex ml-430"
                >
                    Déconnexion
                </button>

        {/* 🔥 BACKGROUND */}
        <div
            className="fixed inset-0 -z-10 bg-cover bg-center"
            style={{
                backgroundImage: "url('/audit_bank.png')"
            }}
        />
        <div className="fixed inset-0 -z-10 bg-white/40 backdrop-blur-[10px]"></div>


        {/* 🔥 CONTENU CENTRÉ */}
        <div className="max-w-6xl mx-auto space-y-6">

            {/* HEADER */}
            <div className="flex items-center justify-center rounded-2xl p-5 mb-6 
            bg-gradient-to-r from-blue-600 via-indigo-600 to-orange-500 shadow-lg">

                <h1 className="text-3xl font-bold text-white tracking-wide">
                    Audit & Traçabilité
                </h1>

            </div>

            
            {/* FILTRES */}
            <div className="flex flex-col md:flex-row gap-4 bg-white/60 backdrop-blur-md p-4 rounded-2xl border border-white/40 shadow-md">

                <input
                    type="text"
                    placeholder="Rechercher..."
                    value={search}
                    onChange={(e)=>setSearch(e.target.value)}
                    className="p-3 rounded-lg w-full md:w-1/3 border border-gray-300 bg-white/80"
                />

                <select
                    value={actionFilter}
                    onChange={(e)=>setActionFilter(e.target.value)}
                    className="p-3 rounded-lg w-full md:w-1/4 border border-gray-300 bg-white/80"
                >
                    <option value="">Toutes les actions</option>
                    {Object.keys(actionLabels).map(action => (
                        <option key={action} value={action}>
                            {actionLabels[action]}
                        </option>
                    ))}
                </select>

                <select
                    value={entityFilter}
                    onChange={(e)=>setEntityFilter(e.target.value)}
                    className="p-3 rounded-lg w-full md:w-1/4 border border-gray-300 bg-white/80"
                >
                    <option value="">Toutes les entités</option>
                    {[...new Set(logs.map(log => log.entityType))].map(entity => (
                        <option key={entity} value={entity}>
                            {entity}
                        </option>
                    ))}
                </select>
                <select
                value={entitySort}
                onChange={(e)=>setEntitySort(e.target.value)}
                className="p-3 rounded-lg w-full md:w-1/4 border border-gray-300 bg-white/80"
                >
                <option value="">Tri entité</option>
                <option value="asc">A → Z</option>
                <option value="desc">Z → A</option>
                </select>
            </div>


            {/* TABLE */}
            <div className="overflow-x-auto rounded-2xl bg-white/50 backdrop-blur-[10px] border border-white/50 shadow-xl">

                <table className="w-full text-[15px]">

                    <thead className="bg-white/40 border-b border-gray-300 text-gray-900">
                        <tr>
                            <th className="p-4 text-left border-r border-gray-300">Date</th>
                            <th className="p-4 text-left border-r border-gray-300">Action</th>
                            <th className="p-4 text-left border-r border-gray-300">Entité</th>
                            <th className="p-4 text-left border-r border-gray-300">ID</th>
                            <th className="p-4 text-left">Utilisateur</th>
                        </tr>
                    </thead>

                    <tbody className="divide-y divide-gray-300">
                        {filteredLogs.map((log,index) => (
                            <tr key={log.idAuditLog || index} className="hover:bg-white/90 transition">

                                <td className="p-4 border-r border-gray-200">
                                    {new Date(log.createdAt).toLocaleString()}
                                </td>

                                <td className="p-4 border-r border-gray-200">
                                    <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getBadgeColor(log.action)}`}>
                                        {actionLabels[log.action] || log.action}
                                    </span>
                                </td>

                                <td className="p-4 border-r border-gray-200">
                                    <span className={`px-3 py-1 rounded-full text-sm font-semibold ${entityBadge(log.entityType)}`}>
                                        {log.entityType}
                                    </span>
                                </td>

                                <td className="p-4 border-r border-gray-200 font-mono">
                                    {log.entityId}
                                </td>

                                <td className="p-4 font-medium">
                                    {log.user?.fullName || "—"}
                                </td>

                            </tr>
                        ))}
                    </tbody>

                </table>
            </div>


            {/* EMPTY */}
            {filteredLogs.length === 0 && (
                <div className="text-center text-gray-600 bg-white/60 p-6 rounded-xl shadow">
                    Aucun log trouvé
                </div>
            )}

        </div>
    </div>
);
}