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
  <div className="relative min-h-screen flex items-center">

    {/* 🔥 BACKGROUND */}
    <div
      className="absolute inset-0 bg-center"
      style={{
        backgroundImage: "url('/bank_human.png')",
        backgroundSize: "100%",
        backgroundRepeat: "no-repeat"
      }}
    />

    {/* 🔥 OVERLAY */}
    <div className="absolute inset-0 bg-gradient-to-r from-black/60 via-black/40 to-black/20"></div>

    {/* 🔥 CONTENT */}
    <div className="relative w-full flex items-center ml-250 pr-20 px-6">

      <form
        onSubmit={handleSubmit}
        className="backdrop-blur-xl bg-white/90 shadow-2xl rounded-2xl w-full max-w-md p-8 pt-24 border border-white/30 space-y-5"
      >

        {/* 🔥 HEADER + LOGO */}
        <div className="relative">

          <img
            src="/Cih-bank.png"
            alt="CIH Bank"
            className="absolute -top-24 left-1/2 -translate-x-1/2 w-32 drop-shadow-lg"
          />

          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-800">
              Inscription
            </h1>

            <p className="text-gray-600 text-sm mt-1">
              Créez votre compte sécurisé
            </p>
          </div>

        </div>

        {/* 🔥 INPUTS */}
        <input
          type="text"
          name="fullName"
          placeholder="Nom complet"
          value={form.fullName}
          onChange={handleChange}
          required
          className="w-full border px-4 py-3 rounded-lg bg-white/80 focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <input
          type="email"
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
          className="w-full border px-4 py-3 rounded-lg bg-white/80 focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <input
          type="password"
          name="password"
          placeholder="Mot de passe"
          value={form.password}
          onChange={handleChange}
          required
          className="w-full border px-4 py-3 rounded-lg bg-white/80 focus:ring-2 focus:ring-blue-500 outline-none"
        />

        <input
          type="text"
          name="phone"
          placeholder="Téléphone"
          value={form.phone}
          onChange={handleChange}
          className="w-full border px-4 py-3 rounded-lg bg-white/80 focus:ring-2 focus:ring-blue-500 outline-none"
        />

        {/* 🔥 BUTTON */}
        <button
          type="submit"
          className="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:opacity-90 text-white font-semibold rounded-lg transition shadow-lg"
        >
          S'inscrire
        </button>

        {/* 🔥 MESSAGE */}
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

        {/* 🔥 LOGIN LINK */}
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
  </div>
);
}