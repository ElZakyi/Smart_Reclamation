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
  <div className="fixed inset-0 -z-10 bg-black/20 backdrop-blur-[10px]" />

  {/* 🔥 CONTENT */}
<div className="max-w-5xl mx-auto p-6 space-y-6">

  {/* CARD PRINCIPALE */}
  <div className="rounded-2xl border border-white/30 bg-white/30 backdrop-blur-xl shadow-xl p-6 space-y-6 text-center">

    {/* HEADER */}
    <div>
      <h1 className="text-2xl font-extrabold text-indigo-700">
        Réclamation {reclamation.reference}
      </h1>
    </div>

    {/* TITRE */}
    <div className="rounded-xl bg-blue-100/30 border border-blue-300/30 p-5 flex flex-col items-center">
      <p className="text-sm text-blue-600 font-semibold mb-1">TITRE</p>
      <h2 className="text-xl font-bold text-slate-900">
        {reclamation.title}
      </h2>
    </div>

    {/* PRIORITE + STATUT */}
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-center items-stretch">

      {/* PRIORITE */}
      <div className="rounded-xl bg-orange-100/30 border border-orange-300/30 p-5 flex flex-col items-center justify-center">
        <p className="text-sm text-orange-600 font-semibold mb-2">PRIORITÉ</p>

        <span
          className={`px-4 py-2 rounded-xl font-semibold ${
            reclamation.priority === "CRITICAL"
              ? "bg-red-500/20 text-red-800"
              : reclamation.priority === "HIGH"
              ? "bg-orange-500/20 text-orange-800"
              : reclamation.priority === "MEDIUM"
              ? "bg-yellow-500/20 text-yellow-800"
              : "bg-green-500/20 text-green-800"
          }`}
        >
          {reclamation.priority}
        </span>
      </div>

      {/* STATUT */}
      <div className="rounded-xl bg-blue-100/30 border border-blue-300/30 p-5 flex flex-col items-center justify-center">
        <p className="text-sm text-blue-600 font-semibold mb-2">STATUT</p>

        <span className="px-4 py-2 rounded-xl bg-blue-500/20 text-blue-800 font-semibold">
          {reclamation.status}
        </span>
      </div>

    </div>

    {/* CLIENT + DATE */}
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-center items-stretch">

      {/* CLIENT */}
      <div className="rounded-xl bg-purple-100/30 border border-purple-300/30 p-5 flex flex-col items-center justify-center">
        <p className="text-sm text-purple-600 font-semibold mb-1">CLIENT</p>
        <p className="text-lg font-semibold text-slate-900">
          {reclamation.user.fullName}
        </p>
      </div>

      {/* DATE CLOTURE */}
      {reclamation.closedAt && (
        <div className="rounded-xl bg-green-100/30 border border-green-300/30 p-5 flex flex-col items-center justify-center">
          <p className="text-sm text-green-600 font-semibold mb-1">
            DATE DE CLÔTURE
          </p>

          <p className="text-lg font-semibold text-green-800">
            {new Date(reclamation.closedAt).toLocaleString()}
          </p>
        </div>
      )}

    </div>

    {/* DESCRIPTION */}
    <div className="flex flex-col items-center">
      <p className="text-sm text-slate-600 font-semibold mb-2">
        DESCRIPTION
      </p>

      <div className="rounded-xl bg-white/20 border border-white/20 p-5 text-slate-800 max-w-2xl">
        {reclamation.description}
      </div>
    </div>

  </div>



{/* CARD UPLOAD */}
<div className="rounded-2xl border border-white/30 bg-white/30 backdrop-blur-md shadow-xl p-6 space-y-6">

  {/* TITLE */}
  <h3 className="text-lg font-bold text-indigo-700 text-center">
    Ajouter une pièce jointe
  </h3>

  {/* DROP ZONE */}
  <label className="block">
  <div
    className="border-2 border-dashed border-indigo-400/50 rounded-xl p-8 text-center bg-white/10 hover:bg-white/20 transition cursor-pointer"
    
    onDragOver={(e) => {
      e.preventDefault(); // 🔥 OBLIGATOIRE
    }}

    onDrop={(e) => {
      e.preventDefault();
      const droppedFile = e.dataTransfer.files[0];
      if (droppedFile) {
        setFile(droppedFile);
      }
    }}
  >

    <div className="flex flex-col items-center gap-3">

      <div className="text-4xl text-indigo-500">⬆️</div>

      {file ? (
        <p className="text-green-600 font-semibold">
          {file.name}
        </p>
      ) : (
        <>
          <p className="text-indigo-600 font-semibold">
            Cliquer pour choisir un fichier
          </p>
          <p className="text-sm text-gray-500">
            ou glisser-déposer ici
          </p>
        </>
      )}

    </div>

    {/* INPUT */}
    <input
      ref={fileInputRef}
      type="file"
      onChange={(e) => setFile(e.target.files[0])}
      className="hidden"
    />

  </div>
</label>

  {/* BUTTONS */}
  <div className="flex justify-center gap-4">

    <button
      onClick={() => {
        setFile(null);
        fileInputRef.current.value = "";
      }}
      className="px-5 py-2 rounded-lg bg-gray-300 text-gray-700 font-medium hover:bg-gray-400 transition"
    >
      Réinitialiser
    </button>

    <button
      onClick={handleUpload}
      disabled={!file}
      className={`px-6 py-2 rounded-lg font-semibold shadow transition ${
        file
          ? "bg-gradient-to-r from-indigo-600 to-blue-500 text-white hover:opacity-90"
          : "bg-gray-300 text-gray-500 cursor-not-allowed"
      }`}
    >
      Ajouter
    </button>

  </div>

</div>
</div>
            {currentUser && (
  <div className="max-w-5xl mx-auto mt-6">
    <div className="rounded-2xl border border-white/30 bg-white/30 backdrop-blur-md shadow-xl p-5">
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