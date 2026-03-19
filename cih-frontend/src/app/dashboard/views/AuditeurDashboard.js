"use client"

import { useEffect, useState } from "react";
import api from "@/services/api";

export default function AuditeurDashboard(){

    const [logs,setLogs] = useState([]);
    const [filteredLogs,setFilteredLogs] = useState([]);
    const [loading,setLoading] = useState(true);

    const [search,setSearch] = useState("");
    const [actionFilter,setActionFilter] = useState("");
    const [entityFilter,setEntityFilter] = useState("");

    useEffect(()=>{
        loadAudit();
    },[]);

    useEffect(()=>{
        filterLogs();
    },[logs,search,actionFilter,entityFilter]);

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
        REFUSE_PLAFOND_CHANGE: "Refus plafond"
    };

    const filterLogs = () => {
        let filtered = logs;

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

        if (actionFilter) {
            filtered = filtered.filter(log => log.action === actionFilter);
        }

        if (entityFilter) {
            filtered = filtered.filter(log => log.entityType === entityFilter);
        }

        setFilteredLogs(filtered);
    }

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
        <div className="p-6 space-y-6">

            {/* HEADER */}
            <h1 className="text-3xl font-bold text-gray-800">
                📊 Audit Logs
            </h1>
            <button
                onClick={handleLogout}
                className="ml-auto flex px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 text-white font-semibold shadow-md transition active:scale-95"
            >
                Déconnexion
            </button>

            {/* 🔍 FILTRES */}
            <div className="flex flex-col md:flex-row gap-4">

                {/* SEARCH */}
                <input
                    type="text"
                    placeholder="Rechercher (action, utilisateur, entité...)"
                    value={search}
                    onChange={(e)=>setSearch(e.target.value)}
                    className="border p-2 rounded w-full md:w-1/3"
                />

                {/* FILTER ACTION */}
                <select
                    value={actionFilter}
                    onChange={(e)=>setActionFilter(e.target.value)}
                    className="border p-2 rounded w-full md:w-1/4"
                >
                    <option value="">Toutes les actions</option>
                    {Object.keys(actionLabels).map(action => (
                        <option key={action} value={action}>
                            {actionLabels[action]}
                        </option>
                    ))}
                </select>

                {/* FILTER ENTITY */}
                <select
                    value={entityFilter}
                    onChange={(e)=>setEntityFilter(e.target.value)}
                    className="border p-2 rounded w-full md:w-1/4"
                >
                    <option value="">Toutes les entités</option>
                    {[...new Set(logs.map(log => log.entityType))].map(entity => (
                        <option key={entity} value={entity}>
                            {entity}
                        </option>
                    ))}
                </select>
            </div>

            {/* TABLE */}
            <div className="overflow-x-auto rounded-lg shadow">
                <table className="w-full text-sm border">

                    <thead className="bg-gray-100 text-gray-700">
                        <tr>
                            <th className="p-3 text-left">Date</th>
                            <th className="p-3 text-left">Action</th>
                            <th className="p-3 text-left">Entité</th>
                            <th className="p-3 text-left">ID</th>
                            <th className="p-3 text-left">Utilisateur</th>
                        </tr>
                    </thead>

                    <tbody>
                        {filteredLogs.map((log,index) => (
                            <tr key={log.idAuditLog || index} className="border hover:bg-gray-50">

                                <td className="p-3">
                                    {new Date(log.createdAt).toLocaleString()}
                                </td>

                                <td className="p-3">
                                    <span className={`px-2 py-1 rounded text-xs font-semibold ${getBadgeColor(log.action)}`}>
                                        {actionLabels[log.action] || log.action}
                                    </span>
                                </td>

                                <td className="p-3">
                                    <span className="bg-gray-100 px-2 py-1 rounded text-xs">
                                        {log.entityType}
                                    </span>
                                </td>

                                <td className="p-3 font-mono">{log.entityId}</td>

                                <td className="p-3">{log.user?.fullName || "—"}</td>
                            </tr>
                        ))}
                    </tbody>

                </table>
            </div>

            {/* EMPTY */}
            {filteredLogs.length === 0 && (
                <p className="text-gray-500">Aucun log trouvé</p>
            )}
        </div>
    )
}