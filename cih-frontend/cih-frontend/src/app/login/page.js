"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/services/api";

export default function LoginPage() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();

  const handleLogin = async () => {
    try {
      const response = await api.post("/auth/login", {
        email,
        password
      });

      localStorage.setItem("token", response.data);
      router.push("/dashboard");

    } catch (error) {
      alert("Identifiants incorrects");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">

      <div className="bg-white shadow-xl rounded-2xl w-[400px] p-8">

        <h1 className="text-3xl font-bold mb-2">
          Connexion
        </h1>

        <p className="text-gray-500 mb-8">
          Accédez à votre espace client
        </p>

        <div className="mb-5">
          <label className="block mb-2 font-medium">
            Identifiant
          </label>
          <input
            type="email"
            placeholder="Votre identifiant"
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="mb-3">
          <label className="block mb-2 font-medium">
            Mot de passe
          </label>
          <input
            type="password"
            placeholder="Votre mot de passe"
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>


        <button
          onClick={handleLogin}
          className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition duration-200"
        >
          Se connecter
        </button>

      </div>
    </div>
  );
}
