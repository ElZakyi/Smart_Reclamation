"use client"
import api from "@/services/api";
import { useRouter } from "next/navigation";
import { useEffect, useRef, useState } from "react"
import ReclamationChat from "../components/ReclamationChat";

export default function ReclamationDetail({id}) {
    const [reclamation,setReclamation] = useState(null);
    const [attachments, setAttachments] = useState([]);
    const [message, setMessage] = useState("");
    const [file,setFile] = useState(null);
    const fileInputRef = useRef(null);
    const [currentUser,setCurrentUser] = useState(null);
    const [routingSuggestion, setRoutingSuggestion] = useState(null)
    const router = useRouter();

    useEffect(()=> {
        loadReclamation(id);
        loadAttachments(id);
        loadRoutingSuggestion(id);
        const user = JSON.parse(localStorage.getItem("user"));
        setCurrentUser(user);
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
    const loadRoutingSuggestion = async(id) => {
        try {
            const res = await api.get(`routing/reclamation/${id}`);
            setRoutingSuggestion(res.data);
        }catch(error){
            setMessage(error.response?.data?.error || "Error /GET get attachments : " + error)
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
        <div className="min-h-screen relative">

  {/* 🔥 BACKGROUND IMAGE */}
  <div
    className="fixed inset-0 -z-10 bg-cover bg-no-repeat scale-100"
    style={{
      backgroundImage: "url('/reclamation_cih.png')",
    }}
  />

  {/* 🔥 OVERLAY */}
  <div className="fixed inset-0 -z-10 bg-white/20 backdrop-blur-[10px]" />

  {/* 🔥 CONTENT */}
<div className="max-w-5xl mx-auto p-6 space-y-8">

  {/* CARD RÉCLAMATION */}
  <div className="rounded-2xl border border-white/30 bg-white/10 backdrop-blur-md shadow-xl overflow-hidden">

    {/* HEADER */}
    <div className="px-6 py-5 border-b border-white/20 bg-white/20 backdrop-blur-md text-center">
      <h1 className="text-2xl font-extrabold text-indigo-700">
        Réclamation {reclamation.reference}
      </h1>
    </div>

    {/* BODY */}
    <div className="p-6">
      <div className="rounded-xl bg-white/20 border border-white/20 p-6 text-slate-900">
        <div className="space-y-5 text-center">

          <div>
            <span className="font-semibold text-gray-700">Titre :</span>{" "}
            <span className="font-semibold">{reclamation.title}</span>
          </div>

          <div>
            <span className="font-semibold text-gray-700">Priorité :</span>{" "}
            <span
            className={`inline-flex px-3 py-1.5 text-sm rounded-full font-semibold ${
                reclamation.priority === "CRITICAL"
                ? "bg-red-500/20 text-red-800 border border-red-400"
                : reclamation.priority === "HIGH"
                ? "bg-orange-500/20 text-orange-800 border border-orange-400"
                : reclamation.priority === "MEDIUM"
                ? "bg-yellow-500/20 text-yellow-800 border border-yellow-400"
                : reclamation.priority === "LOW"
                ? "bg-green-500/20 text-green-800 border border-green-400"
                : "bg-gray-200 text-gray-700"
            }`}
            >
            {reclamation.priority}
            </span>
          </div>

          <div>
            <span className="font-semibold text-gray-700">Statut :</span>{" "}
            <span className="inline-flex px-3 py-1.5 text-sm rounded-full bg-blue-200/70 text-blue-900 font-medium">
              {reclamation.status}
            </span>
          </div>

          {reclamation.closedAt && (
            <div>
              <span className="font-semibold text-gray-700">Date de clôture :</span>{" "}
              <span className="font-semibold text-green-700">
                {new Date(reclamation.closedAt).toLocaleString()}
              </span>
            </div>
          )}

          <div>
            <span className="font-semibold text-gray-700">Client :</span>{" "}
            <span>{reclamation.user.fullName}</span>
          </div>

          <div>
            <span className="font-semibold text-gray-700">Description :</span>
            <div className="mt-3 rounded-lg bg-white/20 border border-white/20 p-4 leading-relaxed whitespace-pre-wrap text-slate-800">
              {reclamation.description}
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>

  {/* CARD ATTACHMENTS */}
  <div className="rounded-2xl border border-white/30 bg-white/10 backdrop-blur-md shadow-xl p-6">

    <h2 className="text-xl font-bold text-indigo-700 mb-4 text-center">
      Pièces jointes
    </h2>

    {attachments.length === 0 ? (
      <p className="text-gray-600 text-center italic">
        Aucune pièce jointe.
      </p>
    ) : (
      <ul className="space-y-4">
        {attachments.map((attachment) => (
          <li
            key={attachment.idAttachment}
            className="flex flex-col md:flex-row md:items-center md:justify-between gap-3 rounded-xl bg-white/20 border border-white/20 p-4 hover:bg-white/30 transition"
          >
            <span className="text-slate-900 font-semibold break-words">
              {attachment.fileName}
            </span>

            <div className="flex flex-wrap gap-2 justify-center md:justify-end">
              <a
                href={`http://localhost:8081/${attachment.storageUrl}`}
                target="_blank"
                className="px-4 py-2 text-sm font-semibold text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 shadow transition"
              >
                Télécharger
              </a>

              <button
                onClick={() => deleteAttachment(attachment.idAttachment)}
                className="px-4 py-2 text-sm font-semibold text-white bg-red-500 rounded-lg hover:bg-red-600 shadow transition"
              >
                Supprimer
              </button>
            </div>
          </li>
        ))}
      </ul>
    )}
  </div>

  {/* CARD UPLOAD */}
  <div className="rounded-2xl border border-white/30 bg-white/10 backdrop-blur-md shadow-xl p-6">

    <h3 className="text-lg font-bold text-indigo-700 mb-4 text-center">
      Ajouter une pièce jointe
    </h3>

    <form
      onSubmit={handleUpload}
      className="flex flex-col md:flex-row gap-4 items-center justify-center"
    >
      <input
        ref={fileInputRef}
        type="file"
        onChange={(e) => setFile(e.target.files[0])}
        className="block w-full md:w-auto text-sm text-gray-700
                   file:mr-4 file:py-2 file:px-4
                   file:rounded-lg file:border-0
                   file:text-sm file:font-semibold
                   file:bg-indigo-600 file:text-white
                   hover:file:bg-indigo-700
                   bg-white/20 border border-white/30 rounded-lg p-2 backdrop-blur-sm"
      />

      <button
        type="submit"
        disabled={!file}
        className={`px-6 py-2 rounded-lg font-semibold shadow transition ${
          file
            ? "bg-orange-600 hover:bg-indigo-700 text-white"
            : "bg-gray-300 text-gray-500 cursor-not-allowed"
        }`}
      >
        Ajouter
      </button>
    </form>
  </div>

</div>
            {currentUser && (
  <div className="max-w-5xl mx-auto mt-6">
    <div className="rounded-2xl border border-white/30 bg-white/10 backdrop-blur-md shadow-xl p-5">
      <ReclamationChat
        reclamationId={id}
        currentUser={currentUser}
      />
    </div>
  </div>
)}

{/* MESSAGE */}
{message && (
  <div className="max-w-5xl mx-auto mt-6">
    <div className="rounded-2xl border border-white/30 bg-white/10 backdrop-blur-md shadow-xl px-5 py-4">

      <div className="flex items-start gap-3">

        {/* ICON */}
        <div className="h-10 w-10 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white grid place-items-center font-bold shadow">
          i
        </div>

        {/* TEXT */}
        <div className="text-sm text-slate-900">
          <div className="font-bold text-indigo-700">
            infos
          </div>

          <div className="mt-1 text-slate-700">
            {message}
          </div>
        </div>

      </div>

    </div>
  </div>
)}

        </div>
    )
}