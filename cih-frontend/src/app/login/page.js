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
      setMessage("Email ou mot de passe incorrect");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-6">

      <div className="bg-white shadow-xl rounded-2xl w-full max-w-md p-8 border space-y-6">

        <div>
          <h1 className="text-3xl font-bold text-gray-800">
            Connexion
          </h1>
          <p className="text-gray-500 text-sm mt-1">
            Accédez à votre espace
          </p>
        </div>

        <div>
          <label className="block mb-1 text-sm font-medium">
            Email
          </label>
          <input
            type="email"
            placeholder="Votre email"
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none"
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
            className="w-full px-4 py-3 rounded-lg border focus:ring-2 focus:ring-blue-500 outline-none"
          />
        </div>

        <button
          onClick={handleLogin}
          className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition"
        >
          Se connecter
        </button>

        {/* ✅ MESSAGE */}
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
  );
}