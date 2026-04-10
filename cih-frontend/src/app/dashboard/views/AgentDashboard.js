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
        const [proposalLimit, setProposalLimit] = useState("");
        const [openPlafondFormId, setOpenPlafondFormId] = useState(null);
        const [teamId, setTeamId] = useState(null);

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
                console.log("Teams:", res.data);
                console.log("PlafondTeam:", plafondTeam);
                if (plafondTeam) {
                    setIsPlafondAgent(true);
                    setTeamId(plafondTeam.team.idTeam); // ✅
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
        const createPlafondProposal = async (requestId) => {
        try {
            await api.post(
                `/plafond-proposal/request/${requestId}/user/${user.idUser}`,
                {
                    proposedLimit: proposalLimit,
                    justification: proposalJustification
                }
            );

            setMessage("Proposition envoyée avec succès");
            setOpenPlafondFormId(null);
            setProposalLimit("");
            setProposalJustification("");

            // 🔥 SOLUTION RADICALE
            getUserTeams(); // recharge tout proprement // refresh

        } catch (error) {
            setMessage(error.response?.data?.error || "Erreur /POST création de proposition : " + error);
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

           <div
  className="min-h-screen bg-fixed bg-cover bg-center relative"
  style={{
    backgroundImage: "url('/agent_cih.png')"
  }}
>
  {/* Overlay */}
  <div className="absolute inset-0 backdrop-blur-[10px] bg-black/20"></div>

  <div className="relative z-10 p-8">
    <button
      onClick={handleLogout}
      className="ml-auto flex px-4 py-2 rounded-xl bg-red-500/90 hover:bg-red-600 text-white font-semibold shadow-md transition active:scale-95"
    >
      Déconnexion
    </button>

    <div className="max-w-5xl mx-auto">
      {isPlafondAgent ? (
        <>
          <div className="relative flex items-center justify-center rounded-2xl p-5 mb-6 
bg-gradient-to-r from-blue-600 via-orange-300 to-orange-500 shadow-lg">

  <h1 className="text-2xl md:text-3xl font-bold text-white tracking-wide">
    Traitement des demandes de plafonds
  </h1>

  <img
    src="/Cih-bank.png"
    alt="CIH Bank"
    className="absolute left-6 top-1/2 -translate-y-1/2 w-26 h-26 object-contain"
  />

</div>

          {plafondRequests.length === 0 && (
            <div className="bg-white/70 backdrop-blur-lg border border-slate-200 p-6 rounded-3xl shadow text-center text-slate-700">
              Aucune demande disponible
            </div>
          )}

          <div className="space-y-6">
            {plafondRequests.map((p) => (
              <div
                key={p.idPlafondRequest}
                className="rounded-[28px] border border-slate-300/70 bg-white/40 backdrop-blur-[10px] shadow-lg p-6"
              >
                {/* HEADER */}
                <div className="flex items-start justify-between gap-4 mb-6">
                  <div className="flex items-start gap-4">
                    {/* Icône carte */}
                    <div className="h-12 w-12 rounded-2xl bg-blue-100 flex items-center justify-center shrink-0">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        className="h-6 w-6 text-blue-600"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                        strokeWidth="2"
                      >
                        <rect x="3" y="6" width="18" height="12" rx="2" />
                        <path d="M3 10h18" />
                      </svg>
                    </div>

                    <div>
                      <h2 className="text-[20px] font-bold text-slate-900 leading-tight">
                        Carte •••• {p.card?.cardNumberMasked?.slice(-4)}
                      </h2>

                      <div className="mt-2 flex items-center gap-2 text-slate-500 text-sm">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          className="h-4 w-4"
                          fill="none"
                          viewBox="0 0 24 24"
                          stroke="currentColor"
                          strokeWidth="2"
                        >
                          <rect x="3" y="4" width="18" height="18" rx="2" />
                          <path d="M16 2v4M8 2v4M3 10h18" />
                        </svg>
                        <span>{new Date(p.createdAt).toLocaleString()}</span>
                      </div>
                    </div>
                  </div>

                  {/* Badge statut */}
                  <span className="inline-flex items-center gap-2 rounded-full border border-yellow-300 bg-yellow-100 px-4 py-2 text-sm font-medium text-orange-700 whitespace-nowrap">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-4 w-4"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <circle cx="12" cy="12" r="9" />
                      <path d="M12 7v5l3 2" />
                    </svg>
                    {p.status}
                  </span>
                </div>

                {/* BLOCS PLAFOND */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5 mb-5">
                  <div className="border border-black/60 rounded-3xl bg-gray-200/90 p-5">
                    <p className="btext-slate-700 text-sm mb-2">Plafond actuel</p>
                    <p className="text-[20px] font-bold text-slate-900">
                      {p.card?.currentLimit ?? 0} €
                    </p>
                  </div>

                  <div className="border border-black/60 rounded-3xl bg-orange-50/90 p-5">
                    <p className="text-slate-700 text-sm mb-2">Plafond demandé</p>
                    <p className="text-[20px] font-bold text-orange-600">
                      {p.requestedLimit} €
                    </p>
                  </div>
                </div>

                {/* JUSTIFICATION */}
                <div className="border border-black/20 rounded-3xl bg-blue-100/80 p-5">
                  <div className="flex items-center gap-2 text-slate-600 mb-2">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-5 w-5"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <circle cx="12" cy="12" r="9" />
                      <path d="M12 8h.01M11 12h1v4h1" />
                    </svg>
                    <span className="text-sm font-medium">Justification</span>
                  </div>

                  <p className="text-slate-900">{p.justification}</p>
                </div>

                {/* ACTION */}
                <div className="mt-5 flex flex-wrap gap-3">
                  <button
                    onClick={() => setOpenPlafondFormId(p.idPlafondRequest)}
                    className="bg-orange-500 hover:bg-orange-600 text-white px-5 py-2.5 rounded-xl shadow-md transition active:scale-95 font-medium"
                  >
                    Proposer plafond
                  </button>
                </div>

                {/* FORMULAIRE OUVERT */}
                {openPlafondFormId === p.idPlafondRequest && (
                  <div className="mt-5 border-t border-slate-300/60 pt-5">
                    <div className="grid gap-3">
                      <input
                        type="number"
                        placeholder="Nouveau plafond"
                        value={proposalLimit}
                        onChange={(e) => setProposalLimit(e.target.value)}
                        className="bg-gray-100/50 border border-slate-300 p-3 rounded-xl w-full focus:outline-none focus:ring-2 focus:ring-orange-300"
                        />

                        <textarea
                        placeholder="Justification"
                        value={proposalJustification}
                        onChange={(e) => setProposalJustification(e.target.value)}
                        rows={4}
                        className="bg-gray-100/50 border border-slate-300 p-3 rounded-xl w-full focus:outline-none focus:ring-2 focus:ring-orange-300"
                        />

                      <div className="flex gap-3 mt-3 justify-end mr-200">

                    <button
                        onClick={() => createPlafondProposal(p.idPlafondRequest)}
                        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition active:scale-95 text-sm"
                    >
                        Envoyer
                    </button>

                    <button
                        onClick={() => setOpenPlafondFormId(false)}
                        className="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded-lg shadow transition active:scale-95 text-sm"
                    >
                        Annuler
                    </button>

                    </div>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          {message && (
            <div className="mt-6 rounded-2xl border border-blue-300/40 bg-blue-100/40 backdrop-blur-lg px-5 py-4 shadow-lg">
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

      )  : (

                        <>
                <div className="relative flex items-center justify-center rounded-2xl p-5 mb-6 
bg-gradient-to-r from-blue-600 via-orange-300 to-orange-500 shadow-lg">

  <h1 className="text-2xl md:text-3xl font-bold text-white tracking-wide">
    Traitement des réclamations
  </h1>

  <img
    src="/Cih-bank.png"
    alt="CIH Bank"
    className="absolute left-6 top-1/2 -translate-y-1/2 w-30 h-30 object-contain"
  />

</div>

  {assignments.length === 0 && (
    <div className="bg-white/60 backdrop-blur-lg border border-white/40 p-6 rounded-2xl shadow text-center">
      Aucune réclamation assignée
    </div>
  )}

  <div className="space-y-6">

  {assignments.map((assignment) => {

    const r = assignment.reclamation;

    return (

      <div
        key={assignment.idAssignment}
        className="bg-white/40 backdrop-blur-lg border border-white/40 rounded-2xl shadow-lg overflow-hidden"
      >

        {/* HEADER GRADIENT */}
        <div className="flex justify-between items-center px-6 py-5 
        bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600 text-white">

          <div>
            <p className="text-sm opacity-80">Référence</p>
            <h2 className="text-xl font-bold">{r.reference}</h2>
          </div>

          <span className={`bg-white/80 text-indigo-700 px-4 py-1 rounded-xl text-sm font-semibold`}>
            {r.status}
          </span>
        </div>

        {/* BODY */}
        <div className="p-6 space-y-6">

          {/* TITRE */}
          <div className="bg-blue-100/40 border border-blue-300/40 rounded-2xl p-5">
            <p className="text-sm text-blue-600 font-semibold mb-1">TITRE</p>
            <h3 className="text-xl font-bold text-slate-900">{r.title}</h3>
          </div>

          {/* DESCRIPTION */}
          <div>
            <p className="text-sm text-slate-500 font-semibold mb-2">DESCRIPTION</p>
            <div className="bg-gray-100/80 border border-gray-300 rounded-2xl p-4 text-slate-800">
              {r.description}
            </div>
          </div>

          {/* INFOS */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

            <div className="bg-blue-100/60 border border-blue-300 rounded-2xl p-4">
              <p className="text-sm text-blue-600 font-semibold mb-1">CANAL</p>
              <p className="text-lg font-bold text-blue-800">{r.canal}</p>
            </div>

            <div className="bg-green-100/60 border border-green-300 rounded-2xl p-4">
              <p className="text-sm text-green-600 font-semibold mb-1">TYPE</p>
              <p className="text-lg font-bold text-green-800">{r.type}</p>
            </div>

            <div className="bg-red-100/60 border border-red-300 rounded-2xl p-4">
              <p className="text-sm text-red-600 font-semibold mb-1">PRIORITÉ</p>
              <p className="text-lg font-bold text-red-800">{r.priority}</p>
            </div>

          </div>

          {/* ================== RESOLUTION ================== */}
          {r.status !== "RESOLUE" && (

            <div className="mt-6">

              <div className="flex items-center gap-4 mb-6">
                <div className="flex-1 h-px bg-gray-300"></div>
                <p className="text-sm font-semibold text-slate-500">RÉSOLUTION</p>
                <div className="flex-1 h-px bg-gray-300"></div>
              </div>

              <div className="bg-purple-100/40 border border-purple-300 rounded-2xl p-6">

                <p className="text-purple-800 font-semibold mb-3">
                  Votre résolution ici
                </p>

                <textarea
                  className="w-full bg-white border border-purple-300 rounded-2xl p-4 
                  focus:outline-none focus:ring-2 focus:ring-purple-400"
                  rows="3"
                  value={contents[r.idReclamation] || ""}
                  onChange={(e) =>
                    handleContentChange(r.idReclamation, e.target.value)
                  }
                  placeholder="Décrivez votre solution ou réponse à cette réclamation..."
                />

              </div>

              {/* BUTTONS */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-6">

                <button
                  onClick={() => {
                    createResolution(r.idReclamation);
                    setProposalFormId(r.idReclamation); // ✅ FIX BUG
                  }}
                  className="bg-orange-500 hover:bg-orange-600 text-white 
                  px-6 py-4 rounded-2xl font-bold shadow-md transition"
                >
                  ✔️ Valider la résolution
                </button>

                <button
                  onClick={() =>
                    setOpenChatId(
                      openChatId === r.idReclamation ? null : r.idReclamation
                    )
                  }
                  className="bg-blue-600 hover:bg-blue-700 text-white 
                  px-6 py-4 rounded-2xl font-bold shadow-md transition"
                >
                  💬 Conversation
                </button>

              </div>

            </div>

          )}

          {/* ================== PROPOSITION ================== */}
          {(r.status === "RESOLUE" || proposalFormId === r.idReclamation) && (

            <div className="mt-6">

              <div className="flex items-center gap-4 mb-6">
                <div className="flex-1 h-px bg-gray-300"></div>
                <p className="text-sm font-semibold text-slate-500">
                  PROPOSITION DE DÉCISION
                </p>
                <div className="flex-1 h-px bg-gray-300"></div>
              </div>

              <div className="bg-white/10 backdrop-blur-md border border-white/40 p-6 rounded-2xl">

                <select
                  onChange={(e) => setProposalDecisionType(e.target.value)}
                  className="w-full bg-white border border-gray-300 rounded-xl px-4 py-3 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
                >
                  <option value="CLOTURE">Clôture</option>
                  <option value="REJET">Rejet</option>
                </select>

                <textarea
                  onChange={(e) => setProposalJustification(e.target.value)}
                  className="w-full bg-white border border-gray-300 rounded-xl p-4 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-400"
                  placeholder="Justification..."
                />

                <button
                  onClick={() => createDecisionProposal(user.idUser, r.idReclamation)}
                  className="bg-orange-500 hover:bg-orange-600 text-white px-6 py-3 rounded-xl font-semibold shadow-md transition"
                >
                  Envoyer la proposition
                </button>

              </div>

            </div>

          )}

          {/* CHAT (affichage) */}
          {openChatId === r.idReclamation && (
            <div className="mt-4 bg-white/20 backdrop-blur-lg border border-white/40 rounded-2xl p-4">
              <ReclamationChat
                reclamationId={r.idReclamation}
                currentUser={user}
              />
            </div>
          )}

        </div>

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
            </div>

        );
    }