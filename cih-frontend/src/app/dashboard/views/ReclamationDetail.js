"use client"
import api from "@/services/api";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react"

export default function ReclamationDetail({id}) {
    const [reclamation,setReclamation] = useState(null);
    const [attachments, setAttachments] = useState([]);
    const [message, setMessage] = useState("");
    const [file,setFile] = useState(null);
    const fileInputRef = useRef(null);
    const router = useRouter();

    useEffect(()=> {
        loadReclamation(id);
        loadAttachments(id);
        console.log(attachments);
    },[]);

    const loadReclamation = async (id) => {
        try {
            const res = await api.get(`/reclamations/${id}`);
            setReclamation(res.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Error /GET reclamation : " + error);
        }
    } 

    const loadAttachments = async (id) => {
        try {
            const response = await api.get(`/attachment/reclamation/${id}`);
            setAttachments(response.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Error /GET get attachments : " + error) ;
        }
    }

    const handleUpload = async (e) => {
        e.preventDefault();
        if(!file) return;

        const formData = new FormData();
        formData.append("file",file);

        try {
            const res = await api.post(`/attachment/reclamation/${id}/user/${reclamation.user.idUser}`,formData);
            setMessage(res.data);
            loadAttachments(id);
            setFile(null);
            if(fileInputRef.current) fileInputRef.current.value = "";
        }catch(error){
            setMessage(error.response?.data?.error || "Error /POST create attachment : " + error) ;
        }
    }
    const deleteAttachment = async(idAttachment) => {
        try {
            const res = await api.delete(`/attachment/${idAttachment}`);
            setMessage(res.data);
            loadAttachments(id);
        }catch(error){
            setMessage(error.response?.data?.error || "Error /DELETE delete attachment : " + error);
        }
    }

    if (!reclamation) 
        return <div className="p-6 text-center text-gray-500 text-lg">Chargement...</div>;

    return (
        <div className="max-w-4xl mx-auto p-6 space-y-8">

            {/* Card Réclamation */}
            <div className="bg-white shadow-lg rounded-xl p-6 border border-gray-200">
                <h1 className="text-2xl font-bold text-indigo-700 mb-6">
                    Réclamation {reclamation.reference}
                </h1>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-gray-700">

                    <div>
                        <p className="text-sm text-gray-500">Titre</p>
                        <p className="font-semibold">{reclamation.title}</p>
                    </div>

                    <div>
                        <p className="text-sm text-gray-500">Statut</p>
                        <span className="px-3 py-1 text-sm rounded-full bg-blue-100 text-blue-700 font-medium">
                            {reclamation.status}
                        </span>
                    </div>

                    <div>
                        <p className="text-sm text-gray-500">Priorité</p>
                        <span className={`px-3 py-1 text-sm rounded-full font-medium
                            ${reclamation.priority === "HIGH" 
                                ? "bg-red-100 text-red-700" 
                                : "bg-yellow-100 text-yellow-700"}
                        `}>
                            {reclamation.priority}
                        </span>
                    </div>

                    <div>
                        <p ><span className="text-sm text-black-500"><strong>Client Nom&Prenom :</strong> </span>{reclamation.user.fullName}</p>
                        <p ><span className="text-sm text-black-500"><strong>Client Email :</strong> </span>{reclamation.user.email  }</p>

                    </div>

                </div>

                {/* Description */}
                <div className="mt-6">
                    <p className="text-sm text-gray-500">Description</p>
                    <div className="mt-2 p-4 bg-gray-50 rounded-lg border text-gray-700">
                        {reclamation.description}
                    </div>
                </div>
            </div>

            {/* Card Attachments */}
            <div className="bg-white shadow-md rounded-xl p-6 border border-gray-200">
                <h2 className="text-xl font-semibold text-gray-800 mb-4">
                    Pièces jointes
                </h2>

                {attachments.length === 0 ? (
                    <p className="text-gray-500">Aucune pièce jointe.</p>
                ) : (
                    <ul className="space-y-3">
                        {attachments.map((attachment)=>(
                            <li 
                            key={attachment.idAttachment}
                            className="flex justify-between items-center bg-gray-50 p-4 rounded-lg border hover:shadow-md transition duration-200"
                            >
                            {/* Nom fichier */}
                            <span className="text-gray-800 font-medium truncate max-w-xs">
                                {attachment.fileName}
                            </span>

                            {/* Actions */}
                            <div className="flex items-center gap-3">

                                <a
                                href={`http://localhost:8081/${attachment.storageUrl}`}
                                target="_blank"
                                className="px-4 py-2 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition duration-200"
                                >
                                Télécharger
                                </a>

                                <button
                                onClick={() => deleteAttachment(attachment.idAttachment)}
                                className="px-4 py-2 text-sm font-semibold text-white bg-red-500 rounded-lg hover:bg-red-600 transition duration-200"
                                >
                                Supprimer
                                </button>

                            </div>
                            </li>
                        ))}
                    </ul>
                )}
            </div>

            {/* Card Upload */}
            <div className="bg-white shadow-md rounded-xl p-6 border border-gray-200">
                <h3 className="text-lg font-semibold text-gray-800 mb-4">
                    Ajouter une pièce jointe
                </h3>

                <form 
                    onSubmit={handleUpload}
                    className="flex flex-col md:flex-row gap-4 items-start md:items-center"
                >
                    <input 
                        ref = {fileInputRef}
                        type="file" 
                        onChange={(e)=>setFile(e.target.files[0])}
                        className="block w-full text-sm text-gray-600
                                   file:mr-4 file:py-2 file:px-4
                                   file:rounded-lg file:border-0
                                   file:text-sm file:font-semibold
                                   file:bg-indigo-50 file:text-indigo-700
                                   hover:file:bg-indigo-100"
                    />

                    <button
                    type="submit"
                    disabled={!file}
                    className={`px-6 py-2 rounded-lg transition duration-200
                        ${file ? "bg-indigo-600 hover:bg-indigo-700 text-white" : "bg-gray-300 text-gray-500 cursor-not-allowed"}`}
                    >
                    Ajouter
                    </button>
                </form>
            </div>

            {/* Message */}
            {message && (
                <div className="p-4 bg-green-50 text-green-700 rounded-lg border border-green-200">
                    {message}
                </div>
            )}

        </div>
    )
}