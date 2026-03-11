"use client"

import api from "@/services/api";
import { useRouter } from "next/navigation";
import {useEffect, useState } from "react";

export default function ResponsableDashboard({user}){

    const [reclmations, setReclamation] = useState([]);
    const [suggestions, setSuggestions] = useState({});
    const [message, setMessage] = useState("");
    const router = useRouter();

    useEffect(()=>{
        getReclamations();
    },[])

    const getReclamations = async () => {
        try {
            const res = await api.get("/reclamations/pending");
            setReclamation(res.data);
            res.data.forEach(reclamation => loadRoutingSuggestions(reclamation.idReclamation));
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur récupération réclamations");
        }
    }

    const loadRoutingSuggestions = async (idReclamation) => {
        try {
            const res = await api.get(`/routing/reclamation/${idReclamation}`);
            setSuggestions(prev =>({
                ...prev,
                [idReclamation]: res.data
            }));
        }catch(error){
            setMessage("Erreur récupération suggestion IA");
        }
    }

    const acceptSuggestion = async (idRouting) => {
        try{
            await api.post(`/assignment/accept/${idRouting}/responsable/${user.idUser}`);
            setMessage("Suggestion IA acceptée !");
            getReclamations();
        }catch(error){
            setMessage("Erreur acceptation suggestion");
        }
    }

    const rejectSuggestion = async (idSuggestion) => {
        try {
            await api.post(`/assignment/reject/${idSuggestion}/responsable/${user.idUser}`);
            setMessage("Suggestion IA refusée !");
            getReclamations();
        }catch(error){
            setMessage("Erreur refus suggestion");
        }
    }
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

                <h1 className="text-3xl font-bold mb-6 text-gray-800">
                    Tableau de bord : Responsable
                </h1>
                
                <div className="space-y-6">

                    {reclmations.map((reclamation)=>{

                        const suggestion = suggestions[reclamation.idReclamation];

                        return(

                            <div
                                key={reclamation.idReclamation}
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

                                {suggestion && (

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
                                                onClick={()=>acceptSuggestion(suggestion.idRouting)}
                                                className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded transition"
                                            >
                                                Accepter
                                            </button>

                                            <button
                                                onClick={()=>rejectSuggestion(suggestion.idRouting)}
                                                className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded transition"
                                            >
                                                Refuser
                                            </button>

                                        </div>

                                    </div>

                                )}

                            </div>

                        )

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

    )

}