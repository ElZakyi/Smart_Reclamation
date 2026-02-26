"use client"

import api from "@/services/api";
import { redirect, useRouter } from "next/navigation";
import { useEffect, useState } from "react"
import AdminDashboard from "./views/AdminDashboard";
import ClientDashboard from "./views/ClientDashboard";
import AgentDashboard from "./views/AgentDashboard";
import ResponsableDashboard from "./views/ResponsableDashboard";
import AuditeurDashboard from "./views/AuditeurDashboard";

export default function DashboardPage(){
    const [user,setUser] = useState(null);
    const router = useRouter();
    
    useEffect(()=>{
        const token = localStorage.getItem("token");
        if(!token){
            router.push('/login');
            return;
        }
        api.get("/auth/me").then((res)=>{setUser(res.data)}).catch(()=>{router.push("/login")});
    },[]);
    if(!user) return <div className="p-10">Loading...</div>
    const role = user.roles[0];
    switch(role){
        case "ADMIN":
            return <AdminDashboard user={user}/>;
        case "CLIENT":
            return <ClientDashboard user={user}/>;
        case "AGENT":
            return <AgentDashboard user={user}/>;
        case "RESPONSABLE":
            return <ResponsableDashboard user={user}/>;
        case "AUDITEUR":
            return <AuditeurDashboard user={user}/>
        default : 
            return <div>Role inconnue</div>
    }
}