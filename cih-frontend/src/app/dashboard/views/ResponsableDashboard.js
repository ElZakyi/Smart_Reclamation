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

        <div
  className="min-h-screen bg-cover bg-center bg-fixed relative"
  style={{
    backgroundImage: "url('/responsable_cih_2.png')",
  }}
>

  {/* 🔥 OVERLAY (PLUS LÉGER) */}
  <div className="absolute inset-0 backdrop-blur-[3px] "></div>

  {/* 🔥 BOUTON DECONNEXION */}
  <div className="absolute top-6 right-6 z-20">
    <button
      onClick={handleLogout}
      className="px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 
      text-white font-semibold shadow-md transition active:scale-95"
    >
      Déconnexion
    </button>
  </div>

  {/* 🔥 CONTENT */}
  <div className="relative z-10 px-6 py-8 bg-black/20">

    <div className="w-full max-w-6xl mx-auto backdrop-blur-sm bg-white/20
    border border-white/20  rounded-3xl shadow-2xl p-8">

      {/* HEADER */}
      <div className="relative flex items-center justify-center rounded-2xl p-5 mb-6 
        bg-gradient-to-r from-blue-600 via-orange-300 to-orange-500 shadow-lg">

        {/* LOGO (positionné à gauche) */}
        <img
            src="/Cih-bank.png"
            alt="CIH Bank"
            className="absolute left-6 w-30 h-30 object-contain"
        />

        {/* TITRE */}
        <h1 className="text-2xl md:text-3xl font-bold text-white tracking-wide">
            Tableau de bord Responsable
        </h1>

        </div>

      <div className="space-y-6">

        {/* Sous titre */}
        <div className="flex items-center justify-center rounded-xl p-4 
        bg-white/20 backdrop-blur-lg rounded-xl p-4 border border-white/20 shadow">
          <h2 className="text-xl font-bold text-slate-800">
            Suggestions intelligentes (IA)
          </h2>
        </div>

        <div className="flex gap-6 overflow-x-auto pb-4 mt-4 items-start">

          {suggestions.length === 0 && (
            <div className="min-w-[320px] bg-white/30 backdrop-blur-md p-6 rounded-2xl shadow border border-white/20 text-center flex-shrink-0">
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
                className="min-w-[360px] bg-white/20 backdrop-blur-lg shadow-lg rounded-2xl p-5 border border-white/20 flex-shrink-0 self-start"
              >

            {/* HEADER */}
            <div className="text-center space-y-2 mb-4">

              <div>
                <p className="text-sm text-slate-500">Référence</p>
                <h3 className="font-semibold text-slate-900">
                  {reclamation.reference}
                </h3>
              </div>

              <div>
                <p className="text-sm text-slate-500">Titre</p>
                <h2 className="text-lg font-bold text-slate-900">
                  {reclamation.title}
                </h2>
              </div>

            </div>

            {/* DESCRIPTION */}
            <div className="bg-white/20 backdrop-blur-md rounded-xl p-4 mb-4">
              <p className="text-sm text-slate-500 mb-1">Description</p>
              <p className="text-slate-800">
                {reclamation.description}
              </p>
            </div>

            {/* SUGGESTION IA */}
            <div className="bg-white/5 backdrop-blur-md border border-white/20">

              <h3 className="font-semibold text-slate-700 mb-4 text-center">
                Suggestion IA
              </h3>

              <div className="grid grid-cols-3 gap-3 text-sm">

                {/* Team */}
                <div className="bg-purple-100/80 rounded-xl p-3 text-center">
                  <p className="text-slate-600 text-xs mb-1">Équipe</p>
                  <p className="font-semibold text-purple-700">
                    {suggestion.suggestedTeam?.name}
                  </p>
                </div>

                {/* Agent */}
                <div className="bg-indigo-100/80 rounded-xl p-3 text-center">
                  <p className="text-slate-600 text-xs mb-1">Agent</p>
                  <p className="font-semibold text-indigo-700">
                    {suggestion.suggestedUser?.fullName}
                  </p>
                </div>

                {/* Score */}
                <div className="bg-green-100/80 rounded-xl p-3 text-center">
                  <p className="text-slate-600 text-xs mb-1">Score IA</p>
                  <p className="font-semibold text-green-700">
                    {suggestion.score?.toFixed(2)}
                  </p>
                </div>

              </div>
                    <div className="flex justify-center gap-4 mt-4">

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

                    <div className="bg-white/20 backdrop-blur-md border border-white/20">

                        <h2 className="font-semibold text-slate-700 mb-4 text-center">
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
                                className="px-4 py-2 rounded-lg bg-orange-600 hover:bg-orange-700 text-white font-medium disabled:bg-gray-400 transition"
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
                    {/* TITRE */}
<div className="flex items-center justify-center rounded-xl p-4 mt-10
bg-white/20 backdrop-blur-md border border-white/20 shadow">
  <h2 className="text-xl font-bold text-slate-800">
    Décisions en attentes
  </h2>
</div>

{/* LISTE */}
<div className="flex gap-6 overflow-x-auto pb-4 mt-4 items-start">

  {reclamationsForDecision.length === 0 && (
    <div className="min-w-[320px] bg-white/10 backdrop-blur-md border border-white/20 p-6 rounded-2xl shadow text-center flex-shrink-0">
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
  className="min-w-[350px] bg-white/10 backdrop-blur-xl border border-white/20 
  rounded-2xl shadow-lg p-6 transition flex-shrink-0 self-start"
>
  {/* HEADER */}
  {/* HEADER */}
<div className="text-center mb-4">

  {/* Référence */}
  <div className="mb-3">
    <p className="text-sm text-slate-500">Référence</p>
    <h3 className="font-semibold text-slate-900">
      {rec.reference}
    </h3>
  </div>

  {/* TITRE */}
  <div className="mb-3">
    <p className="text-sm text-slate-500">Titre</p>
    <h3 className="text-lg font-bold text-slate-900">
      {rec.title}
    </h3>
  </div>

  {/* STATUS → 🔥 maintenant ici */}
  <div>
    <p className="text-sm text-slate-500">Statut</p>
    <span className="inline-block mt-1 text-xs bg-yellow-100/80 text-yellow-700 px-3 py-1 rounded-full">
      {rec.status}
    </span>
  </div>

</div>

  {/* DESCRIPTION */}
  <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 mb-4">
    <p className="text-sm text-slate-500 mb-1">Description</p>
    <p className="text-slate-800">
      {rec.description}
    </p>
  </div>

  {/* PROPOSITION */}
  <div className="bg-white/10 backdrop-blur-md border border-white/20 p-4 rounded-xl">

    <h4 className="font-semibold text-slate-700 mb-4 text-center">
      Proposition de l’agent
    </h4>

    {/* 🔥 BADGES COMME EN HAUT */}
    <div className="grid grid-cols-2 gap-3 text-sm mb-3">

      <div className="bg-indigo-100/80 rounded-xl p-3 text-center">
        <p className="text-slate-600 text-xs mb-1">Type</p>
        <p className="font-semibold text-indigo-700">
          {proposal.type}
        </p>
      </div>

      <div className="bg-gray-100/80 rounded-xl p-3 text-center">
        <p className="text-slate-600 text-xs mb-1">Agent</p>
        <p className="font-semibold text-gray-700">
          {proposal.user?.fullName}
        </p>
      </div>

    </div>

   <div className="bg-white/20 backdrop-blur-md rounded-xl p-3 mt-3">
    <p className="text-gray-700 text-sm italic text-center">
        "{proposal.justification}"
    </p>
    </div>

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

          <div className="mt-4 border-t border-white/20 pt-4">

            <textarea
              placeholder="Motif de votre décision"
              value={motif}
              onChange={(e) => setMotif(e.target.value)}
              className="w-full bg-white/10 backdrop-blur-md border border-white/20 rounded-lg p-3 focus:ring-2 focus:ring-indigo-500 outline-none"
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
                    <div className="flex items-center justify-center rounded-xl p-4 mt-10
bg-white/20 backdrop-blur-md border border-white/20 shadow">
  <h2 className="text-xl font-bold text-slate-800">
    Demandes changement de plafond 
  </h2>
</div>

                    <div className="flex gap-6 overflow-x-auto pb-4 mt-4">
                        {requests.length === 0 && (
                            <div className="min-w-[320px] bg-white/20 backdrop-blur-md border border-white/20 p-6 rounded-xl shadow border border-gray-200 flex-shrink-0 text-center">
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
    className="min-w-[320px] bg-white/10 backdrop-blur-xl border border-white/20 
    rounded-2xl shadow-lg p-6 flex flex-col gap-5 flex-shrink-0"
  >

    {/* HEADER */}
    <div className="text-center space-y-2">

      <div>
        <p className="text-sm text-slate-500">Carte</p>
        <h3 className="font-semibold text-slate-900">
          {request.card.cardNumberMasked}
        </h3>
      </div>

      <div>
        <p className="text-sm text-slate-500">Statut</p>
        <span className="inline-block mt-1 text-xs bg-yellow-100/80 text-yellow-700 px-3 py-1 rounded-full">
          {request.status}
        </span>
      </div>

    </div>

    {/* INFOS DEMANDE */}
    <div className="bg-white/10 backdrop-blur-md rounded-xl p-4 text-center">
      <p className="text-sm text-slate-500 mb-1">Plafond demandé</p>
      <p className="text-indigo-600 font-semibold text-lg">
        {request.requestedLimit} DH
      </p>
    </div>

    {/* 🔥 PROPOSITION AGENT */}
    <div className="bg-white/10 backdrop-blur-md border border-white/20 p-5 rounded-xl space-y-4">

      <h4 className="font-semibold text-slate-700 text-center">
        Proposition de l’agent
      </h4>

      {/* BADGES */}
      <div className="grid grid-cols-2 gap-4 text-sm">

        <div className="bg-indigo-100/80 rounded-xl p-3 text-center">
          <p className="text-slate-600 text-xs mb-1">Nouveau plafond</p>
          <p className="font-semibold text-indigo-700">
            {r.proposedLimit} DH
          </p>
        </div>

        <div className="bg-gray-100/80 rounded-xl p-3 text-center">
          <p className="text-slate-600 text-xs mb-1">Agent</p>
          <p className="font-semibold text-gray-700">
            {r.user?.fullName}
          </p>
        </div>

      </div>

      {/* JUSTIFICATION */}
<div className="bg-white/20 backdrop-blur-md rounded-xl p-3 text-center">

  <p className="text-xs text-slate-500 mb-1">
    Justification
  </p>

  <p className="text-gray-700 text-sm italic">
    "{r.justification}"
  </p>

</div>

    </div>

    {/* ACTION */}
    <button
      onClick={() => setSelectedPlafondId(r.idPlafondProposal)}
      className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg transition"
    >
      Traiter
    </button>

    {/* FORM */}
    {selectedPlafondId === r.idPlafondProposal && (

      <div className="space-y-4">

        <textarea
          placeholder="Motif de décision..."
          value={motifPlafond}
          onChange={(e) => setMotifPlafond(e.target.value)}
          className="w-full bg-white/10 backdrop-blur-md border border-white/20 rounded-lg p-3 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
        />

        <div className="flex gap-3">

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

      </div>

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
        </div>

    );

}