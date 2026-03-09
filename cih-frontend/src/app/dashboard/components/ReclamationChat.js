"use client"

import { useEffect, useRef, useState } from "react";
import api from "@/services/api";

export default function ReclamationChat({ reclamationId, currentUser }) {

    const [messages, setMessages] = useState([]);
    const [messageInfo,setMessageInfo] = useState("");
    const [type, setType] = useState("COMMENT");
    const [content,setContent] = useState("");
    const messagesEndRef = useRef(null);

    const loadMessages = async () => {
        try {
            const res = await api.get(`/messages/reclamation/${reclamationId}`);
            setMessages(res.data);
        }catch(error){
            setMessageInfo(error.response?.data?.error || "Error /GET get messages : " + error);
        }
    }

    useEffect(() => {

        loadMessages();

        const interval = setInterval(() => {
            loadMessages();
        }, 3000);

        return () => clearInterval(interval);

    }, [reclamationId]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const addMessage = async () => {

        if(!content.trim()) return;

        try{

            await api.post("/messages",{
                idUser : currentUser.idUser,
                idReclamation : reclamationId,
                type : type,
                content : content
            });

            setContent("");
            setMessageInfo("Message envoyé !");
            loadMessages();

        }catch(error){
            setMessageInfo(error.response?.data?.error || "Error /POST add message : " + error);
        }
    }

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };
    
    const getMessageColor = (type) => {

    switch(type){

        case "COMMENT":
            return "bg-gray-200 text-gray-800";

        case "REQUEST_INFO":
            return "bg-orange-200 text-orange-900";

        case "PROVIDE_INFO":
            return "bg-green-200 text-green-900";

        default:
            return "bg-gray-200 text-gray-800";
    }

};

    return (
        <div className="bg-white border rounded-lg p-4 mt-6 shadow">

            <h2 className="font-semibold mb-3">Conversation</h2>

            <div className="h-80 overflow-y-auto p-2 space-y-3 border rounded">

                {messages.map((msg) => {

                    const isMe = msg.user.idUser === currentUser.idUser;

                    return (
                        <div
                        key={msg.idMessage}
                        className={`flex ${isMe ? "justify-end" : "justify-start"}`}
                        >

                        <div
                            className={`max-w-[70%] px-3 py-2 rounded-lg text-sm shadow
                            ${getMessageColor(msg.messageType, isMe)}`}
                        >

                            <div className="text-xs font-semibold mb-1">
                                {msg.user.fullName}
                            </div>

                            <div>{msg.content}</div>

                            <div className="text-[10px] opacity-70 mt-1 flex justify-between">

                                <span>{msg.type}</span>

                                <span>
                                    {new Date(msg.createdAt).toLocaleTimeString()}
                                </span>

                            </div>

                        </div>

                        </div>
                    );

                })}

                <div ref={messagesEndRef} />

            </div>

            {/* Input */}

            <div className="flex gap-2 mt-3">

                <select
                    value={type}
                    onChange={(e)=>setType(e.target.value)}
                    className="border rounded px-2 py-1"
                >
                    <option value="COMMENT">Commentaire</option>
                    <option value="REQUEST_INFO">Demande info</option>
                    <option value="PROVIDE_INFO">Fournir info</option>
                </select>

                <input
                    type="text"
                    value={content}
                    onChange={(e)=>setContent(e.target.value)}
                    placeholder="Ecrire votre message"
                    className="flex-1 border rounded px-2 py-1"
                />

                <button
                    onClick={addMessage}
                    className="bg-blue-600 text-white px-3 py-1 rounded"
                >
                    Envoyer
                </button>

            </div>

            {messageInfo && (
                <div className="text-xs text-gray-500 mt-2">
                    {messageInfo}
                </div>
            )}

        </div>
    );
}