"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/services/api";

export default function LoginPage() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);

  const router = useRouter();

  const handleLogin = async () => {

    try {
      setMessage("");
      setError(false);

      const response = await api.post("/auth/login", {
        email,
        password
      });

      const token = response.data;

      localStorage.setItem("token", token);

      const userResponse = await api.get("/users/me", {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      localStorage.setItem("user", JSON.stringify(userResponse.data));

      router.push("/dashboard");

    } catch (error) {
      setError(true);
      setMessage(
        error.response?.data?.message || 
        error.message || 
        "Erreur inconnue"
      );
    }
  };

  return (
  <div className="relative min-h-screen flex items-center">

    {/* 🔥 BACKGROUND IMAGE */}
    <div
      className="absolute inset-0 bg-center"
      style={{
        backgroundImage: "url('/bank_human.png')",
        backgroundSize: "100%", // 🔥 zoom out
        backgroundRepeat: "no-repeat"
      }}
    />

    {/* 🔥 OVERLAY */}
    <div className="absolute inset-0 bg-gradient-to-r from-black/60 via-black/40 to-black/20"></div>

    {/* 🔥 CONTENT */}
    <div className="relative w-full flex items-center ml-250 pr-20 px-6">

      <div className="backdrop-blur-xl bg-white/90 shadow-2xl rounded-2xl w-full max-w-md p-8 pt-24 border border-white/30 space-y-6">

  <div className="relative">

    {/* 🔥 LOGO */}
    <img
      src="/Cih-bank.png"
      alt="Cih_Bank"
      className="absolute -top-25 left-1/2 -translate-x-1/2 w-32 drop-shadow-lg"
    />

    {/* 🔥 CONTENU */}
    <div className="text-center">
      <h1 className="text-3xl font-bold text-gray-800">
        Connexion
      </h1>

      <p className="text-gray-600 text-sm mt-1">
        Accédez à votre espace sécurisé
      </p>
    </div>

  </div>

        <div>
          <label className="block mb-1 text-sm font-medium">
            Email
          </label>
          <input
            type="email"
            placeholder="Votre email"
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-3 rounded-lg border bg-white/80 focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        <div>
          <label className="block mb-1 text-sm font-medium">
            Mot de passe
          </label>
          <input
            type="password"
            placeholder="Votre mot de passe"
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-3 rounded-lg border bg-white/80 focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        <button
          onClick={handleLogin}
          className="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:opacity-90 text-white font-semibold rounded-lg transition shadow-lg"
        >
          Se connecter
        </button>

        {message && (
          <div className={`rounded-xl px-4 py-3 text-sm flex gap-3 items-start shadow-sm 
            ${error ? "bg-red-50 border border-red-200 text-red-700" 
                    : "bg-green-50 border border-green-200 text-green-700"}`}>
            <div className="font-bold">
              {error ? "!" : "✓"}
            </div>
            <div>{message}</div>
          </div>
        )}

        <p className="text-center text-sm">
          Pas encore de compte ?{" "}
          <span
            onClick={() => router.push("/register")}
            className="text-blue-600 cursor-pointer font-semibold"
          >
            S'inscrire
          </span>
        </p>

      </div>
    </div>
  </div>
);
}