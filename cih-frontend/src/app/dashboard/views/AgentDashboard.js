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
    const router = useRouter();

    useEffect(() => {
        getReclamationForAgent();
    }, []);

    const getReclamationForAgent = async () => {
        try {
            const res = await api.get(`/assignment/agent/${user.idUser}`);
            setAssignments(res.data);
        } catch (error) {
            setMessage("Erreur récupération réclamations");
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
            getReclamationForAgent();

        } catch (error) {

            setMessage(
                error.response?.data?.error ||
                "Erreur création résolution"
            );
        }
    };
    const handleLogout = () => {
      localStorage.removeItem("token");
      router.push("/login");
    }
    return (

        <div className="min-h-screen bg-gray-100 p-8">
            <button
                onClick={handleLogout}
                className="flex ml-auto px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 
                        text-white font-semibold shadow-md transition 
                        active:scale-95"
            >
                Déconnexion
            </button>

            <div className="max-w-4xl mx-auto">

                <h1 className="text-3xl font-bold mb-8 text-gray-800">
                    Mes réclamations à traiter
                </h1>

                {assignments.length === 0 && (
                    <div className="bg-white p-6 rounded shadow text-center">
                        Aucune réclamation assignée
                    </div>
                )}

                <div className="space-y-6">

                    {assignments.map((assignment) => {

                        const r = assignment.reclamation;

                        return (

                            <div
                                key={assignment.idAssignment}
                                className="bg-white rounded-lg shadow p-6 border"
                            >

                                <div className="flex justify-between mb-2">

                                    <span className="font-semibold text-gray-700">
                                        {r.reference}
                                    </span>

                                    <span className="text-xs px-3 py-1 bg-blue-100 text-blue-700 rounded">
                                        {r.priority}
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

                                    <span className="bg-yellow-200 px-3 py-1 rounded">
                                        Status : {r.status}
                                    </span>

                                </div>

                                <div className="mt-5">

                                    <textarea
                                        className="w-full border rounded p-3 focus:outline-none focus:ring"
                                        rows="4"
                                        placeholder="Rédiger la résolution..."
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
                                        className="mt-3 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded transition"
                                    >
                                        Valider la résolution
                                    </button>
                                    <button
                                        onClick={() => 
                                            setOpenChatId(
                                                openChatId === r.idReclamation ? null : r.idReclamation
                                            )
                                        }
                                        className="mt-3 bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded"
                                    >
                                        {openChatId ? "Fermer conversation" : "Ouvrir conversation"}
                                    </button>

                                </div>
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

            </div>

        </div>

    );
}