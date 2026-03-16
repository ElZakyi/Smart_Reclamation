"use client"

import api from "@/services/api";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function ResponsableDashboard({ user }) {
    const [suggestions, setSuggestions] = useState([]);
    const [message, setMessage] = useState("");
    const [manualSuggestionId,setManualSuggestionId] = useState(null);
    const [teams,setTeams] = useState([]);
    const [members,setMembers] = useState([]);
    const [selectedTeam,setSelectedTeam] = useState(null);
    const [selectedAgent,setSelectedAgent] = useState(null);
    const router = useRouter();

    useEffect(() => {
        getSuggestions();
    }, []);

    const getSuggestions = async () => {
        try {
            const res = await api.get("/routing/pending");
            setSuggestions(res.data);
        } catch (error) {
            setMessage("Erreur récupération suggestions IA");
        }
    };
    const getTeams = async () => {
        try {
            const res = await api.get("/team");
            setTeams(res.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /GET récupération des équipes : " + error);
        }
    }
    const getMembersOfTeam = async (idTeam) => {
        try {
            const res = await api.get(`/user-team/${idTeam}`);
            setMembers(res.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /GET récupération des membres : " + error);
        }
    } 

    const acceptSuggestion = async (idRouting) => {
        try {
            const res = await api.post(`/assignment/accept/${idRouting}/responsable/${user.idUser}`);
            setMessage("Suggestion IA acceptée et la team : " + res.data.team.name + " a été notifiée !");
            getSuggestions();
        } catch (error) {
            setMessage("Erreur acceptation suggestion");
        }
    };

    const rejectSuggestion = async (idRouting) => {
        try {
            await api.post(`/assignment/reject/${idRouting}/responsable/${user.idUser}`);
            setMessage("Suggestion IA refusée !");
        } catch (error) {
            setMessage("Erreur refus suggestion");
        }
    };
    const manualAssign = async (idSuggestion) => {
        const idResponsable = user.idUser;

        try {
            await api.post(
            `/assignment/manual/${idSuggestion}/responsable/${idResponsable}?idTeam=${selectedTeam}&idAgent=${selectedAgent}`
            );

            setMessage("Réclamation assignée manuellement");

            setManualSuggestionId(null);
            setSelectedTeam(null);
            setSelectedAgent(null);

            getSuggestions();

        } catch(error){
            setMessage(error.response?.data?.error || "Erreur assignation manuelle");
        }
    }
    const handleLogout = () => {
        localStorage.removeItem("token");
        router.push("/login");
    };

    return (

        <div className="min-h-screen bg-gray-100 p-8">

            <button
                onClick={handleLogout}
                className="flex ml-auto px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 
                text-white font-semibold shadow-md transition active:scale-95"
            >
                Déconnexion
            </button>

            <div className="max-w-4xl mx-auto">

                <h1 className="text-3xl font-bold mb-6 text-gray-800">
                    Tableau de bord : Responsable
                </h1>

                <div className="space-y-6">
                    {suggestions.length === 0 && (
                        <div className="bg-white rounded-lg shadow p-6 text-center border">
                            <h2 className="text-lg font-semibold text-gray-700">
                                Aucune suggestion IA disponible
                            </h2>
                            <p className="text-gray-500 mt-2">
                                Toutes les réclamations ont déjà été traitées ou aucune suggestion n'est en attente.
                            </p>
                        </div>
                    )}

                    {suggestions.map((suggestion) => {

                        const reclamation = suggestion.reclamation;

                        return (

                            <div
                                key={suggestion.idRouting}
                                className="bg-white shadow-md rounded-lg p-5 border border-gray-200"
                            >

                                <div className="flex justify-between items-center mb-2">

                                    <span className="font-semibold text-gray-700">
                                        {reclamation.reference}
                                    </span>

                                    <span className="text-xs bg-gray-200 px-2 py-1 rounded">
                                        Réclamation
                                    </span>

                                </div>

                                <h2 className="text-lg font-semibold text-gray-800">
                                    {reclamation.title}
                                </h2>

                                <p className="text-gray-600 mt-1">
                                    {reclamation.description}
                                </p>

                                <div className="mt-4 p-4 bg-gray-50 rounded border">

                                    <h3 className="font-semibold text-gray-700 mb-3">
                                        Suggestion IA
                                    </h3>

                                    <div className="flex flex-wrap gap-4 text-sm mb-4">

                                        <span className="bg-purple-100 text-purple-700 px-3 py-1 rounded">
                                            Team : {suggestion.suggestedTeam?.name}
                                        </span>

                                        <span className="bg-indigo-100 text-indigo-700 px-3 py-1 rounded">
                                            Agent : {suggestion.suggestedUser?.fullName}
                                        </span>

                                        <span className="bg-green-100 text-green-700 px-3 py-1 rounded">
                                            Score IA : {suggestion.score?.toFixed(2)}
                                        </span>

                                    </div>

                                    <div className="flex gap-3">

                                        <button
                                            onClick={() => acceptSuggestion(suggestion.idRouting)}
                                            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded transition"
                                        >
                                            Accepter
                                        </button>

                                        <button
                                            onClick={async () => {
                                                await rejectSuggestion(suggestion.idRouting);
                                                await getTeams();
                                                setManualSuggestionId(suggestion.idRouting);
                                            }}
                                            className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded transition"
                                        >
                                            Refuser
                                        </button>

                                    </div>

                                </div>
                                {manualSuggestionId === suggestion.idRouting && (

                                <div className="mt-5 bg-white border rounded-lg p-5 shadow-sm">

                                    <h2 className="text-lg font-semibold text-gray-800 mb-4">
                                        Assignation manuelle
                                    </h2>

                                    {/* Team selection */}

                                    <div className="mb-4">

                                        <label className="block text-sm font-medium text-gray-700 mb-1">
                                            Choisir une équipe
                                        </label>

                                        <select
                                            onChange={(e) => {
                                                const teamId = e.target.value;
                                                setSelectedTeam(teamId);
                                                getMembersOfTeam(teamId);
                                            }}
                                            className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                                        >

                                            <option value="">Sélectionner une équipe</option>

                                            {teams.map((team) => (
                                                <option key={team.idTeam} value={team.idTeam}>
                                                    {team.name}
                                                </option>
                                            ))}

                                        </select>

                                    </div>

                                    {/* Agent selection */}

                                    {selectedTeam && (

                                        <div className="mb-4">

                                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                                Choisir un agent
                                            </label>

                                            <select
                                                onChange={(e) => setSelectedAgent(e.target.value)}
                                                className="w-full border rounded-lg px-3 py-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                                            >

                                                <option value="">Sélectionner un agent</option>

                                                {members
                                                    .filter(member =>
                                                        member.user.userRoles?.some(
                                                            ur => ur.role.name === "AGENT"
                                                        )
                                                    )
                                                    .map(member => (
                                                        <option
                                                            key={member.user.idUser}
                                                            value={member.user.idUser}
                                                        >
                                                            {member.user.fullName}
                                                        </option>
                                                    ))}

                                            </select>

                                        </div>

                                    )}

                                    {/* Buttons */}

                                    <div className="flex gap-3">

                                        <button
                                            onClick={() => setManualSuggestionId(null)}
                                            className="px-4 py-2 rounded-lg bg-gray-200 hover:bg-gray-300 text-gray-700 transition"
                                        >
                                            Annuler
                                        </button>

                                        <button
                                            disabled={!selectedTeam || !selectedAgent}
                                            onClick={() => manualAssign(suggestion.idRouting)}
                                            className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-700 text-white font-medium disabled:bg-gray-400 transition"
                                        >
                                            Valider l’assignation
                                        </button>

                                    </div>

                                </div>

                            )}

                            </div>

                        );

                    })}

                </div>

                {message && (
                    <div className="mt-6 rounded-2xl border border-blue-200 bg-gradient-to-r from-blue-50 to-indigo-50 px-5 py-4 shadow-sm">
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