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
    const [reclamationsForDecision, setReclamationsForDecision] = useState([]);
    const [selectedDecision,setSelectedDecision] = useState(null);
    const [motif,setMotif]=useState("");
    const router = useRouter();
    const [requests,setRequests] = useState([]);
    const [motifPlafond,setMotifPlafond] = useState("");
    const [selectedPlafondId, setSelectedPlafondId] = useState(null);

    useEffect(() => {
        getSuggestions();
        getReclamationForDecision();
        getRequestsForValidation();
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
    const getReclamationForDecision = async () => {
        try {
            const res = await api.get("/decision-proposal/decision");
            setReclamationsForDecision(res.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /GET récupération des réclamation en attente de décision : " + error);
        }
    }
    const acceptDecisionProposal = async(idReclamation,idProposal) => {
        try{
            await api.post(`/decision/accept/reclamation/${idReclamation}/user/${user.idUser}/proposal/${idProposal}`,motif);
            setMessage("Proposition de décision à été accéptée par le responsable et le client sera notifié !");
            getReclamationForDecision();
            setMotif("");
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /POST accéptation de proposition : " + error);
        }
    }
    const rejectDecisionProposal = async(idReclamation, idProposal) => {
        try{
            await api.post(`/decision/reject/reclamation/${idReclamation}/user/${user.idUser}/proposal/${idProposal}`,motif);
            setMessage("Proposition de décision à été refusée par le responsable !");
            getReclamationForDecision();
            setMotif("");
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /POST refus de proposition : " + error);
        }
    }
    const getRequestsForValidation = async () => {
        try{
        const res = await api.get("/plafond-proposal/validation");
        setRequests(res.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /GET récupération demandes changement de plafond : " + error);
        }
    };
    const handleDecision = async (id, outcome) => {
        try {
            await api.post(`/plafond-decision/request/${id}/user/${user.idUser}`, {
                outcome: outcome,
                motif: motifPlafond
            });

            setMessage("Décision envoyée");
            getRequestsForValidation();

        } catch (error) {
            setMessage(error.response?.data?.error || "Erreur /POST création de décision : " + error);
        }
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

            <div className="w-full px-8">

                <h1 className="text-3xl font-bold mb-6 text-gray-800">
                    Tableau de bord : Responsable
                </h1>

                <div className="space-y-6">

                    <h2 className="text-2xl font-bold mt-10 text-gray-800">
    Suggestions IA
</h2>

<div className="flex gap-6 overflow-x-auto pb-4 mt-4">

    {suggestions.length === 0 && (
        <div className="min-w-[320px] bg-white p-6 rounded-xl shadow border text-center flex-shrink-0">
            <p className="text-gray-500 font-medium">
                Aucune suggestion IA disponible
            </p>
        </div>
    )}

    {suggestions.map((suggestion) => {

        const reclamation = suggestion.reclamation;

        return (

            <div
                key={suggestion.idRouting}
                className="min-w-[360px] bg-white shadow-md rounded-lg p-5 border border-gray-200 flex-shrink-0"
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
                    <h2 className="text-2xl font-bold mt-10 text-gray-800">
                        Décisions en attente
                    </h2>

                    <div className="flex gap-6 overflow-x-auto pb-4 mt-4">

                        {reclamationsForDecision.length === 0 && (
                            <div className="min-w-[320px] bg-white p-6 rounded-xl shadow border text-center flex-shrink-0">
                                <p className="text-gray-500 font-medium">
                                    Aucune décision en attente
                                </p>
                            </div>
                        )}

    {reclamationsForDecision.map((proposal) => {

        const rec = proposal.reclamation;

        return (

            <div
                key={proposal.idDecisionProposal}
                className="min-w-[350px] bg-white rounded-xl shadow-md p-6 border border-gray-200 hover:shadow-lg transition flex-shrink-0"
            >

                {/* HEADER */}
                <div className="flex justify-between items-center mb-3">

                    <span className="font-semibold text-gray-700">
                        {rec.reference}
                    </span>

                    <span className="text-xs bg-yellow-100 text-yellow-700 px-2 py-1 rounded">
                        {rec.status}
                    </span>

                </div>

                {/* RECLAMATION */}
                <h3 className="text-lg font-bold text-gray-800">
                    {rec.title}
                </h3>

                <p className="text-gray-600 mt-1">
                    {rec.description}
                </p>

                {/* PROPOSAL */}
                <div className="mt-4 bg-indigo-50 border border-indigo-200 p-4 rounded-lg">

                    <h4 className="font-semibold text-indigo-700 mb-2">
                        Proposition de l’agent
                    </h4>

                    <div className="flex flex-wrap gap-3 text-sm mb-3">

                        <span className="bg-indigo-100 text-indigo-700 px-3 py-1 rounded">
                            Type : {proposal.type}
                        </span>

                        <span className="bg-gray-100 text-gray-700 px-3 py-1 rounded">
                            Agent : {proposal.user?.fullName}
                        </span>

                    </div>

                    <p className="text-gray-700 text-sm italic">
                        "{proposal.justification}"
                    </p>

                </div>

                {/* ACTION */}
                <div className="mt-4">

                    <button
                        onClick={() => setSelectedDecision(proposal.idDecisionProposal)}
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg transition"
                    >
                        Prendre décision
                    </button>

                </div>

                {/* FORM */}
                {selectedDecision === proposal.idDecisionProposal && (

                    <div className="mt-4 border-t pt-4">

                        <textarea
                            placeholder="Motif de votre décision"
                            value={motif}
                            onChange={(e) => setMotif(e.target.value)}
                            className="w-full border rounded-lg p-3 focus:ring-2 focus:ring-indigo-500 outline-none"
                        />

                        <div className="flex gap-3 mt-3">

                            <button
                                onClick={() =>
                                    acceptDecisionProposal(
                                        rec.idReclamation,
                                        proposal.idDecisionProposal
                                    )
                                }
                                className="flex-1 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg"
                            >
                                 Accepter
                            </button>

                            <button
                                onClick={() =>
                                    rejectDecisionProposal(
                                        rec.idReclamation,
                                        proposal.idDecisionProposal
                                    )
                                }
                                className="flex-1 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg"
                            >
                                Refuser
                            </button>

                        </div>

                    </div>

                                )}

                            </div>

                        );

                    })}

                </div>
                    <h2 className="text-2xl font-bold mt-10 text-gray-800">
                     Demande changement de plafond en attente
                    </h2>

                    <div className="flex gap-6 overflow-x-auto pb-4 mt-4">
                        {requests.length === 0 && (
                            <div className="min-w-[320px] bg-white p-6 rounded-xl shadow border border-gray-200 flex-shrink-0 text-center">
                                <p className="text-gray-600 font-medium">
                                    Aucune demande de changement de plafond en attente
                                </p>
                                <p className="text-sm text-gray-400 mt-2">
                                    Toutes les demandes ont été traitées
                                </p>
                            </div>
                        )}

                        {requests.map((r) => {

                        const request = r.plafondRequest;
                        if (!request) return null;
    return (

        <div
            key={r.idPlafondProposal}
            className="min-w-[320px] bg-white p-6 rounded-xl shadow border border-gray-200 flex-shrink-0"
        >

            {/* HEADER */}
            <div className="mb-3 flex justify-between items-center">
                <span className="font-semibold text-gray-700">
                    Carte
                </span>

                <span className="text-xs px-3 py-1 rounded bg-yellow-100 text-yellow-700">
                    {request.status}
                </span>
            </div>

            {/* INFOS DEMANDE */}
            <div className="space-y-2 text-sm text-gray-700">
                <p>
                    <b>Numéro :</b> {request.card.cardNumberMasked}
                </p>

                <p>
                    <b>Plafond demandé :</b>
                    <span className="text-indigo-600 font-semibold ml-1">
                        {request.requestedLimit} DH
                    </span>
                </p>
            </div>

            {/* 🔥 PROPOSITION AGENT */}
            <div className="mt-4 bg-indigo-50 border border-indigo-200 p-4 rounded-lg">

                <h4 className="font-semibold text-indigo-700 mb-2">
                    Proposition de l’agent
                </h4>

                <div className="flex flex-wrap gap-3 text-sm mb-2">

                    <span className="bg-indigo-100 text-indigo-700 px-3 py-1 rounded">
                        Nouveau plafond : {r.proposedLimit} DH
                    </span>

                    <span className="bg-gray-100 text-gray-700 px-3 py-1 rounded">
                        Agent : {r.user?.fullName}
                    </span>

                </div>

                <p className="text-gray-700 text-sm italic">
                    "{r.justification}"
                </p>

            </div>

            {/* BOUTON */}
            <button
                onClick={() => setSelectedPlafondId(r.idPlafondProposal)}
                className="mt-4 bg-indigo-600 text-white px-4 py-2 rounded-lg w-full"
            >
                Traiter
            </button>

            {/* FORM */}
            {selectedPlafondId === r.idPlafondProposal && (

                <>
                    <textarea
                        placeholder="Motif de décision..."
                        value={motifPlafond}
                        onChange={(e) => setMotifPlafond(e.target.value)}
                        className="w-full border rounded-lg p-2 mt-4 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
                    />

                    <div className="flex gap-3 mt-4">

                        <button
                            onClick={() => handleDecision(r.idPlafondProposal, "VALIDE")}
                            className="flex-1 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg transition"
                        >
                            Valider
                        </button>

                        <button
                            onClick={() => handleDecision(r.idPlafondProposal, "REFUSE")}
                            className="flex-1 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition"
                        >
                            Refuser
                        </button>

                        <button
                            onClick={() => {
                                setSelectedPlafondId(null);
                                setMotifPlafond("");
                            }}
                            className="flex-1 bg-gray-300 px-4 py-2 rounded-lg"
                        >
                            Annuler
                        </button>

                    </div>
                </>

            )}

        </div>

    );

})}

                        </div>
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