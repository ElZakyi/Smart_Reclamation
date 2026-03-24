"use client"

import api from "@/services/api";
import { useEffect, useState } from "react";
import ReclamationChat from "../components/ReclamationChat";
import { useRouter } from "next/navigation";

export default function AgentDashboard({ user }) {

    const [assignments, setAssignments] = useState([]);
    const [contents, setContents] = useState({});
    const [message, setMessage] = useState("");
    const [openChatId, setOpenChatId] = useState(null);
    const [proposalFormId, setProposalFormId] = useState(null);
    const [proposalJustification, setProposalJustification] = useState("");
    const [proposalDecisionType, setProposalDecisionType] = useState("CLOTURE");
    const [isPlafondAgent, setIsPlafondAgent] = useState(false);
    const [plafondRequests, setPlafondRequests] = useState([]); 

    const router = useRouter();

    useEffect(() => {
        getUserTeams();
    }, []);

    const getUserTeams = async () => {
        try {
            const res = await api.get(`/user-team/user/${user.idUser}`);

            const plafondTeam = res.data.find(
                (ut) => ut.team.name === "PLAFOND_TEAM"
            );

            if (plafondTeam) {
                setIsPlafondAgent(true);
                getPlafondRequests(plafondTeam.team.idTeam);
            } else {
                getReclamationForAgent();
            }

        } catch (error) {
            setMessage("Erreur récupération teams");
        }
    };

    const getPlafondRequests = async (teamId) => {
        try {
            const res = await api.get(`/plafond-requests/team/${teamId}`);
            setPlafondRequests(res.data);
        } catch (error) {
            setMessage("Erreur récupération plafond");
        }
    };

    const getReclamationForAgent = async () => {
        try {
            const res = await api.get(`/assignment/agent/${user.idUser}`);
            setAssignments(res.data);
        } catch (error) {
            setMessage("Erreur récupération réclamations : " + error);
        }
    };

    const handleContentChange = (id, value) => {
        setContents(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const createResolution = async (idReclamation) => {
        try {

            await api.post(
                `/resolution/reclamation/${idReclamation}/user/${user.idUser}`,
                contents[idReclamation]
            );

            setMessage("Résolution enregistrée avec succès");

            setContents(prev => ({
                ...prev,
                [idReclamation]: ""
            }));

            setProposalFormId(idReclamation);

            getReclamationForAgent();

        } catch (error) {
            setMessage(error.response?.data?.error || "Erreur création résolution : " + error);
        }
    };

    const createDecisionProposal = async (idUser, idReclamation) => {
        try {

            await api.post(
                `/decision-proposal/reclamation/${idReclamation}/user/${idUser}?decisionType=${proposalDecisionType}`,
                { justification: proposalJustification }
            );

            setMessage("Proposition envoyée au responsable");

            setProposalFormId(null);
            setProposalJustification("");

            getReclamationForAgent();

        } catch (error) {
            setMessage(error.response?.data?.error || "Erreur création proposition : " + error);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        router.push("/login");
    };

    const getStatusColor = (status) => {

        switch (status) {

            case "AFFECTEE":
                return "bg-blue-100 text-blue-700";

            case "RESOLUE":
                return "bg-green-100 text-green-700";

            case "EN_VALIDATION":
                return "bg-yellow-100 text-yellow-700";

            case "REJETEE":
                return "bg-red-100 text-red-700";

            default:
                return "bg-gray-200 text-gray-700";
        }
    };

    return (

        <div className="min-h-screen bg-gray-100 p-8">

            <button
                onClick={handleLogout}
                className="ml-auto flex px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 text-white font-semibold shadow-md transition active:scale-95"
            >
                Déconnexion
            </button>

            <div className="max-w-4xl mx-auto">
                {/* 🔥 CONDITION PRINCIPALE */}
                {isPlafondAgent ? (

                    <>
                        <h1 className="text-3xl font-bold mb-8 text-gray-800">
                            Demandes de changement de plafond
                        </h1>

                        {plafondRequests.length === 0 && (
                            <div className="bg-white p-6 rounded-xl shadow text-center">
                                Aucune demande disponible
                            </div>
                        )}

                        <div className="space-y-6">

                            {plafondRequests.map((p) => (

                                <div key={p.idPlafondRequest} className="bg-white rounded-xl shadow p-6">

                                    <div className="flex justify-between mb-2">
                                        <span className="font-semibold">
                                            Carte : {p.card?.cardNumberMasked}
                                        </span>

                                        <span className="bg-yellow-100 text-yellow-700 px-2 py-1 rounded text-xs">
                                            {p.status}
                                        </span>
                                    </div>

                                    <p><b>Nouveau plafond :</b> {p.requestedLimit}</p>
                                    <p><b>Justification :</b> {p.justification}</p>

                                    <p className="text-sm text-gray-500 mt-2">
                                        {new Date(p.createdAt).toLocaleString()}
                                    </p>

                                </div>

                            ))}

                        </div>
                    </>

                ) : (

                    <>
                        <h1 className="text-3xl font-bold mb-8 text-gray-800">
                            Mes réclamations à traiter
                        </h1>

                        {assignments.length === 0 && (
                            <div className="bg-white p-6 rounded-xl shadow text-center">
                                Aucune réclamation assignée
                            </div>
                        )}

                        <div className="space-y-6">

                            {assignments.map((assignment) => {

                                const r = assignment.reclamation;

                                return (

                                    <div
                                        key={assignment.idAssignment}
                                        className="bg-white rounded-xl shadow p-6 border border-gray-200"
                                    >

                                        {/* Header */}
                                        <div className="flex justify-between items-center mb-2">

                                            <span className="font-semibold text-gray-700">
                                                {r.reference}
                                            </span>

                                            <span className={`text-xs px-3 py-1 rounded ${getStatusColor(r.status)}`}>
                                                {r.status}
                                            </span>

                                        </div>

                                        <h2 className="text-lg font-semibold text-gray-800">
                                            {r.title}
                                        </h2>

                                        <p className="text-gray-600 mt-2">
                                            {r.description}
                                        </p>

                                        <div className="flex gap-3 mt-3 text-sm">

                                            <span className="bg-gray-200 px-3 py-1 rounded">
                                                Canal : {r.canal}
                                            </span>

                                            <span className="bg-gray-200 px-3 py-1 rounded">
                                                Type : {r.type}
                                            </span>

                                            <span className="bg-orange-100 text-orange-700 px-3 py-1 rounded">
                                                Priorité : {r.priority}
                                            </span>

                                        </div>

                                        {r.status !== "RESOLUE" && (

                                            <div className="mt-5">

                                                <textarea
                                                    className="w-full border rounded-lg p-3"
                                                    rows="4"
                                                    value={contents[r.idReclamation] || ""}
                                                    onChange={(e) =>
                                                        handleContentChange(
                                                            r.idReclamation,
                                                            e.target.value
                                                        )
                                                    }
                                                />

                                                <button
                                                    onClick={() => createResolution(r.idReclamation)}
                                                    className="mt-3 bg-green-600 text-white px-4 py-2 rounded-lg"
                                                >
                                                    Valider la résolution
                                                </button>

                                            </div>

                                        )}

                                        {(r.status === "RESOLUE" || proposalFormId === r.idReclamation) && (

                                            <div className="mt-6 bg-gray-50 p-4 rounded-lg border">

                                                <select
                                                    onChange={(e) => setProposalDecisionType(e.target.value)}
                                                    className="border rounded px-3 py-2 mb-3 w-full"
                                                >
                                                    <option value="CLOTURE">Clôture</option>
                                                    <option value="REJET">Rejet</option>
                                                </select>

                                                <textarea
                                                    onChange={(e) => setProposalJustification(e.target.value)}
                                                    className="w-full border rounded-lg p-3 mb-3"
                                                />

                                                <button
                                                    onClick={() => createDecisionProposal(user.idUser, r.idReclamation)}
                                                    className="bg-blue-600 text-white px-4 py-2 rounded-lg"
                                                >
                                                    Envoyer la proposition
                                                </button>

                                            </div>

                                        )}

                                        <button
                                            onClick={() =>
                                                setOpenChatId(
                                                    openChatId === r.idReclamation ? null : r.idReclamation
                                                )
                                            }
                                            className="mt-4 bg-gray-600 text-white px-4 py-2 rounded-lg"
                                        >
                                            Conversation
                                        </button>

                                        {openChatId === r.idReclamation && (
                                            <ReclamationChat
                                                reclamationId={r.idReclamation}
                                                currentUser={user}
                                            />
                                        )}

                                    </div>

                                );

                            })}

                        </div>

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
                    </>

                )}

            </div>

        </div>

    );
}