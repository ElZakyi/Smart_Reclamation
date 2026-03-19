"use client";

import { useState } from "react";
import api from "@/services/api";
import { useRouter } from "next/navigation";

export default function RegisterPage() {

  const router = useRouter();

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    phone: ""
  });

  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await api.post("/auth/register", form);

      setSuccess(true);
      setMessage("Compte créé avec succès !");

      setTimeout(() => {
        router.push("/login");
      }, 1500);

    } catch (error) {
      setSuccess(false);
      setMessage(error.response?.data?.error || "Erreur inscription");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-6">

      <form
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md space-y-5 border"
      >

        <h1 className="text-3xl font-bold text-center text-gray-800">
          Inscription
        </h1>

        <p className="text-center text-gray-500 text-sm">
          Créez votre compte
        </p>

        <input
          type="text"
          name="fullName"
          placeholder="Nom complet"
          value={form.fullName}
          onChange={handleChange}
          required
          className="w-full border px-4 py-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
          className="w-full border px-4 py-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <input
          type="password"
          name="password"
          placeholder="Mot de passe"
          value={form.password}
          onChange={handleChange}
          required
          className="w-full border px-4 py-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <input
          type="text"
          name="phone"
          placeholder="Téléphone"
          value={form.phone}
          onChange={handleChange}
          className="w-full border px-4 py-3 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <button
          type="submit"
          className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition"
        >
          S'inscrire
        </button>

        {/* ✅ MESSAGE */}
        {message && (
          <div className={`rounded-xl px-4 py-3 text-sm flex gap-3 items-start shadow-sm 
            ${success ? "bg-green-50 border border-green-200 text-green-700" 
                      : "bg-red-50 border border-red-200 text-red-700"}`}>

            <div className="font-bold">
              {success ? "✓" : "!"}
            </div>

            <div>{message}</div>
          </div>
        )}

        <p className="text-center text-sm">
          Déjà un compte ?{" "}
          <span
            onClick={() => router.push("/login")}
            className="text-blue-600 cursor-pointer font-semibold"
          >
            Se connecter
          </span>
        </p>

      </form>
    </div>
  );
}