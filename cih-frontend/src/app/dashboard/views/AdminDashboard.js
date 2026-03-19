import api from "@/services/api";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function AdminDashboard({ user }) {

  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [message, setMessage] = useState("");
  const [showCreateUser, setShowCreateUser] = useState(false);
  const [permissions,setPermissions] = useState([]);
  const [selectedRoleIdForPerm,setSelectedRoleIdForPerm] = useState(null);
  const [selectedPermissionId,setSelectedPermissionId] = useState("");
  const [permissionsByRole,setPermissionsByRole] = useState({});
  const [editingUser,setEditingUser] = useState(null);
  const [teams,setTeams] = useState([]);
  
  const [teamForm,setTeamForm] = useState({
    name:"",
    description:""
  });
  const [teamEditing,setTeamEditing] = useState(null);
  const [showTeamForm,setShowTeamForm] = useState(false);
  const [membersOfTeams,setMembersOfTeams] = useState({});
  const [selectedMemberByTeam,setSelectedMemberByTeam] = useState({});
  const router = useRouter();

  const [userForm, setUserForm] = useState({
    fullName: "",
    email: "",
    password: "",
    phone: "",
  });

  const refreshUsers = async () => {
    try {
      const res = await api.get("/users");
      setUsers(res.data);
    } catch(error) {
      setMessage(error.response?.data?.error ||"Erreur /GET récupération utilisateurs : " + error);
    }
  };
  const refreshTeams = async () => {
    try {
          const res = await api.get("/team");
          setTeams(res.data);
        }catch(error){
          setMessage(error.reponse?.data?.error || "Erreur /GET récupération des équipes : " + error);
        }
  }

  useEffect(() => {
    const loadData = async() => {
        await refreshUsers();

        try {
            const res = await api.get("/roles");
            setRoles(res.data);
        }catch(error){
            setMessage(error.response.data.error || "Erreur /GET récupération des rôles :  " + error);
        }
        try {
            const res = await api.get("/permissions");
            setPermissions(res.data);
        }catch(error){
            setMessage(error.reponse?.data?.error || "Error /GET récupération des permissions : " + error);
        }
        await refreshTeams();
        
    };
    loadData();
  }, []);
  useEffect(() => {
    if (teams.length > 0) {
      getMembersOfTeam(teams);
    }

  }, [teams]);

  useEffect(() => {

    const loadAllPermissionsForRoles = async () => {

        const roleIds = new Set();

        users.forEach(user => {
        user.userRoles?.forEach(ur => {
            roleIds.add(ur.role.idRole);
        });
        });

        for (let roleId of roleIds) {
        if (!permissionsByRole[roleId]) {
            try {
            const res = await api.get(`/role-permissions/role/${roleId}`);
            setPermissionsByRole(prev => ({
                ...prev,
                [roleId]: res.data
            }));
            } catch (error) {
              setMessage(error.reponse?.data?.error || "Erreur /GET récupération role-permission : " + error);
            }
        }
        }
    };

    if (users.length > 0) {
        loadAllPermissionsForRoles();
    }

  }, [users]);

  const assignRoleToUser = async (roleId) => {
    if (!roleId) return;

    try {
      const res = await api.post(
        `/user-roles/assign/users/${selectedUser}/roles/${roleId}`
      );
      setMessage(res.data);
      setSelectedUser(null);
      await refreshUsers();
    } catch (error) {
      if (error.response) {
        setMessage(error.response.data.error || "Erreur /POST assigner un rôle à l'utilisateur : " + error);
      } else {
        setMessage("Erreur serveur");
      }
    }
  };

  const handleChange = (e) => {
    setUserForm({
      ...userForm,
      [e.target.name]: e.target.value
    });
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setMessage("");

    try {
      await api.post("/users", userForm);
      setMessage("Utilisateur créé avec succès !");
      setShowCreateUser(false);

      setUserForm({
        fullName: "",
        email: "",
        password: "",
        phone: ""
      });

      await refreshUsers();

    } catch (error) {
      setMessage(error.response?.data?.error || "Erreur /POST créer un utilisateur : " + error);
    }
  };

  const desactivateAccount = async(id) => {
    try{
        const res = await api.patch(`/users/${id}/desactivate`);
        setMessage(res.data);
        await refreshUsers();
    }catch(error){
        setMessage(error.response?.data?.error || "Erreur /PATCH desactiver un utilisateur : " + error);
    }
  }

  const activateAccount = async(id) => {
    try {
        const res = await api.patch(`/users/${id}/activate`);
        setMessage(res.data);
        refreshUsers();
    }catch(error){
        setMessage(error.response?.data?.error || "Erreur /PATCH activater un utilisateur : " + error);
    }
  }

  const affectPermissionToRole = async() => {
    try {
        const res = await api.post(
        `/role-permissions/assign/roles/${selectedRoleIdForPerm}/permissions/${selectedPermissionId}`
        );

        setMessage(res.data);

        // 🔥 Recharge les permissions du rôle
        const updated = await api.get(`/role-permissions/role/${selectedRoleIdForPerm}`);

        setPermissionsByRole(prev => ({
        ...prev,
        [selectedRoleIdForPerm]: updated.data
        }));

        setSelectedPermissionId("");
        setSelectedRoleIdForPerm(null);

    } catch(error){
        setMessage(error.response?.data?.error || "Erreur /POST assigner permission au rôle : " + error);
    }
    };
    const updateRoleUser = async(userId,roleId) => {
        try {
            const res = await api.put(`user-roles/update/users/${userId}/roles/${roleId}`);
            setMessage(res.data);
            setSelectedUser(null);
            await refreshUsers();
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /PUT changer role d'un utilisateur : " + error);
        }
    }
    const deletePermissionFromRole = async(idRole,idPermission) => {
        try {
            const res = await api.delete(`role-permissions/remove/roles/${idRole}/permissions/${idPermission}`);
            setMessage(res.data);
            const updated = await api.get(`/role-permissions/role/${idRole}`);
            setPermissionsByRole(prev => ({
                ...prev,
                [idRole] : updated.data
            }))
        }catch(error){
            setMessage(error.response?.data?.error || "Erreur /DELETE retirer permission au rôle : " + error);
        }
    }
    const updateUser = async () => {
        try {
            const payload = {
            fullName: editingUser.fullName,
            email: editingUser.email,
            phone: editingUser.phone,
            password: editingUser.password,
            };

            const res = await api.put(`/users/${editingUser.idUser}`, payload);

            setMessage(res.data);
            setEditingUser(null);
            await refreshUsers();
        } catch (error) {
            setMessage(error.response?.data?.error || "Erreur /PUT modifier un utilisateur : " + error);
        }
        };
    const handleLogout = () => {
      localStorage.removeItem("token");
      router.push("/login");
    }
    const handleCreateTeam = async(e) => {
      e.preventDefault();
      try {
        await api.post("/team",teamForm);
        setMessage("Team created successfully");
        setTeamForm({name:"",description:""});
        setShowTeamForm(false);
        refreshTeams();
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /POST créer une équipe : " + error);
      }
    }
    const activateTeam = async(idTeam) => {
      try {
        const res = await api.patch(`/team/${idTeam}/activate`);
        setMessage(res.data);
        refreshTeams();
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /POST activer une équipe : " + error);
      }
    }
    const deactivateTeam = async(idTeam) => {
      try {
        const res = await api.patch(`/team/${idTeam}/deactivate`);
        setMessage(res.data);
        refreshTeams();
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /POST desactivater une équipe : " + error);
      }
    }
    const updateTeam = async() => {
      try {
        const playload = {
          name : teamEditing.name,
          description : teamEditing.description
        };
        const res = await api.put(`/team/${teamEditing.idTeam}`,playload);
        setMessage("Team updated sucessfully !");
        setTeamEditing(null);
        refreshTeams();
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /PUT modfier une équipe : " + error);
      }
    }
    const assignMemberToTeam = async(idTeam ,idUser) => {
      try {
        const res = await api.post(`/user-team/${idTeam}/user/${idUser}`);
        setMessage(res.data);
        await getMembersOfTeam(teams);
        setSelectedMemberByTeam(prev => ({
          ...prev,
          [idTeam]: ""
        }));
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /POST assigner un membre à l'équipe : " + error);
      }
    }
    const getMembersOfTeam = async (teamList) => {
      const teamMembers = {};
      for(const team of  teamList){
        try {
          const res = await api.get(`/user-team/${team.idTeam}`);
          teamMembers[team.idTeam] = res.data;
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /GET récupérer membres de l'équipe : " + error);
        teamMembers[team.idTeam] = [];
      }
      }
      setMembersOfTeams(teamMembers);
    }
    const RemoveUserFromTeam = async (userId,teamId) => {
      try {
        const res = await api.delete(`/user-team/${teamId}/user/${userId}`);
        setMessage(res.data);
        await getMembersOfTeam(teams);
      }catch(error){
        setMessage(error.response?.data?.error || "Erreur /DELETE retirer membre de l'équipe : " + error);
      }
    }
  return (
    
    <div className="min-h-screen bg-gray-100 p-6 md:p-10">
      <button
            onClick={handleLogout}
            className="flex ml-auto px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 
                      text-white font-semibold shadow-md transition 
                      active:scale-95"
          >
            Déconnexion
          </button>
      <div className="max-w-7xl mx-auto space-y-8">

        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold text-gray-800">
            Dashboard administrateur :
          </h1>
          <div className="flex items-center gap-3 rounded-2xl border border-slate-200 bg-white/70 backdrop-blur px-4 py-2 shadow-sm">
              <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white grid place-items-center font-bold">
                {(user?.fullName || user?.email || "U").toString().trim().charAt(0).toUpperCase()}
              </div>
              <div className="leading-tight">
                <div className="text-sm font-semibold text-slate-900">
                  {user?.fullName ? user.fullName : "Utilisateur"}
                </div>
                <div className="text-xs text-slate-500">
                  {user?.email ? user.email : "—"}
                </div>
              </div>
            </div>
          
        </div>

        {/* TABLE USERS */}
        <div className="bg-white rounded-xl shadow overflow-hidden border border-gray-200">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50 flex justify-between items-center">
            <h2 className="font-semibold text-center text-gray-700">Utilisateurs</h2>
            <button
            onClick={() => setShowCreateUser(true)}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition active:scale-[0.99]"
          >
            Créer utilisateur
          </button>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Nom complet</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Email</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Téléphone</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Actif</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 bg-white">
                {users.map((u) => (
                  <tr key={u.idUser} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm text-gray-800">{u.fullName}</td>
                    <td className="px-6 py-4 text-sm text-gray-700">{u.email}</td>
                    <td className="px-6 py-4 text-sm text-gray-700">{u.phone ? u.phone : <span className="text-gray-400 italic text-sm">Aucun nombre de téléphone trouvé</span> }</td>
                    <td className="px-6 py-4">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${
                        u.isActive
                          ? "bg-green-100 text-green-700"
                          : "bg-red-100 text-red-700"
                      }`}>
                        {u.isActive ? "Actif" : "Inactif"}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex flex-wrap gap-2">
                        <button
                          onClick={()=>setEditingUser(u)}
                          className="bg-gray-800 hover:bg-gray-900 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                        >
                          Modifier utilisateur
                        </button>

                        <button
                          onClick={() => setSelectedUser(u.idUser)}
                          className="bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                        >
                          {u.userRoles.length > 0 ? "Modifier rôle" : "Affecter rôle"}
                        </button>

                        {u.isActive ? (
                          <button
                            onClick={()=> desactivateAccount(u.idUser)}
                            className="bg-red-600 hover:bg-red-700 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                          >
                            Désactiver
                          </button>
                        ) : (
                          <button
                            onClick={()=> activateAccount(u.idUser)}
                            className="bg-green-600 hover:bg-green-700 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                          >
                            Activer
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* TABLE ROLES + PERMISSIONS */}
        <div className="bg-white rounded-xl shadow overflow-hidden border border-gray-200">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50">
            <h2 className="font-semibold text-center text-gray-700">Rôles & Permissions</h2>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Utilisateur</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Rôle</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Permissions</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">Action</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-200 bg-white">
                {users.map((user)=> (
                  user.userRoles.map((ur)=> (
                    <tr key={`${user.idUser}-${ur.role.idRole}`} className="hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm text-gray-800">{user.fullName}</td>

                      <td className="px-6 py-4 text-sm font-semibold text-gray-900">
                        {ur.role.name || null}
                      </td>

                      <td className="px-6 py-4 text-sm text-gray-600">
                        <div className="space-y-1">
                          {permissionsByRole[ur.role.idRole] &&
                          permissionsByRole[ur.role.idRole].length > 0 ? (

                            permissionsByRole[ur.role.idRole].map((permName) => {
                              const permObj = permissions.find(p => p.name === permName);

                              return (
                                <div key={permName} className="flex items-center justify-between gap-3 rounded-md border border-gray-200 px-3 py-1.5 bg-gray-50">
                                  <span className="text-xs md:text-sm text-gray-700 break-all">{permName}</span>
                                  <button
                                    onClick={() =>
                                      deletePermissionFromRole(
                                        ur.role.idRole,
                                        permObj?.idPermission
                                      )
                                    }
                                    className="text-red-600 hover:text-red-700 text-xs font-semibold"
                                  >
                                    Retirer
                                  </button>
                                </div>
                              );
                            })

                          ) : (
                            <span className="text-gray-400 italic text-sm">
                              Aucune permission attribuée
                            </span>
                          )}
                        </div>
                      </td>

                      <td className="px-6 py-4">
                        <button
                          onClick={()=> {
                            setSelectedRoleIdForPerm(ur.role.idRole);
                            setMessage("");
                          }}
                          className="bg-orange-600 hover:bg-orange-700 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                        >
                          Gérer permissions
                        </button>
                      </td>
                    </tr>
                  ))
                ))}
              </tbody>

            </table>
          </div>
        </div>
        {/* TABLE TEAMS */}
<div className="bg-white rounded-xl shadow overflow-hidden border border-gray-200">
  <div className="px-6 py-4 border-b border-gray-200 bg-gray-50 flex justify-between items-center">
    <h2 className="font-semibold text-gray-700">Teams</h2>
    <button
      onClick={() => setShowTeamForm(true)}
      className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition active:scale-[0.99]"
    >
      Créer Team
    </button>
  </div>

  <div className="overflow-x-auto">
    <table className="min-w-full divide-y divide-gray-200">
      <thead className="bg-gray-50">
        <tr>
          <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">
            Nom
          </th>
          <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">
            Description
          </th>
          <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">
            Statut
          </th>
          <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">
            Membres
          </th>
          <th className="px-6 py-3 text-left text-sm font-semibold text-gray-600">
            Actions
          </th>
        </tr>
      </thead>

      <tbody className="divide-y divide-gray-200 bg-white">
        {teams.map((team) => (
          <tr key={team.idTeam} className="hover:bg-gray-50">
            <td className="px-6 py-4 text-sm text-gray-800 font-medium">
              {team.name}
            </td>
            <td className="px-6 py-4 text-sm text-gray-600">
              {team.description}
            </td>
            <td className="px-6 py-4">
              <span
                className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${
                  team.isActive
                    ? "bg-green-100 text-green-700"
                    : "bg-red-100 text-red-700"
                }`}
              >
                {team.isActive ? "Actif" : "Inactif"}
              </span>
            </td>
            <td className="px-6 py-4">
              {membersOfTeams[team.idTeam] && membersOfTeams[team.idTeam].length > 0 ? (

                <div className="flex flex-col gap-2">

                  {membersOfTeams[team.idTeam].map((member)=> (

                    <div
                      key={member.user.idUser}
                      className="flex items-center justify-between bg-gray-50 border rounded-lg px-3 py-2"
                    >

                      <span className="text-sm text-gray-800 font-medium">
                        {member.user.fullName}
                      </span>

                      <button
                        onClick={()=>RemoveUserFromTeam(member.user.idUser,team.idTeam)}
                        className="text-red-600 hover:text-red-700 text-xs font-semibold"
                      >
                        Retirer
                      </button>

                    </div>

                  ))}

                </div>

              ) : (
                <span className="text-gray-400 italic text-sm">
                  Aucun membre
                </span>
              )}
            </td>
            <td className="px-6 py-4">
              <div className="flex gap-2 flex-wrap">
                <button
                  onClick={() => setTeamEditing(team)}
                  className="bg-gray-800 hover:bg-gray-900 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                >
                  Modifier
                </button>

                {team.isActive ? (
                  <button
                    onClick={() => deactivateTeam(team.idTeam)}
                    className="bg-red-600 hover:bg-red-700 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                  >
                    Désactiver
                  </button>
                ) : (
                  <button
                    onClick={() => activateTeam(team.idTeam)}
                    className="bg-green-600 hover:bg-green-700 text-white px-3 py-1.5 rounded-md text-sm shadow-sm transition active:scale-[0.99]"
                  >
                    Activer
                  </button>
                )}
                <select
                  value={selectedMemberByTeam[team.idTeam] || ""}
                  onChange={(e)=>{
                    const userId = e.target.value;

                    setSelectedMemberByTeam(prev => ({
                      ...prev,
                      [team.idTeam]: userId
                    }));

                    assignMemberToTeam(team.idTeam,userId);
                  }}

                  className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-400"
                >
                  <option>Assigner membre</option>
                  {users.map((user)=>(
                    <option key = {user.idUser} value = {user.idUser}>{user.fullName}</option>
                  ))}
                </select>
              </div>
            </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>

        {/* MODAL UPDATE USER */}
        {editingUser && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
            <div className="w-full max-w-xl rounded-xl bg-white shadow-xl border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-800">Modifier utilisateur</h3>
                <button
                  onClick={()=> setEditingUser(null)}
                  className="text-gray-500 hover:text-gray-700"
                >
                  ✕
                </button>
              </div>

              <div className="p-6 space-y-4">
                <input
                  type="text"
                  placeholder="Nom et prenom"
                  value={editingUser.fullName}
                  onChange={(e)=> setEditingUser({...editingUser,fullName:e.target.value})}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                />

                <input
                  type="text"
                  placeholder="Email"
                  value={editingUser.email}
                  onChange={(e)=> setEditingUser({...editingUser,email:e.target.value})}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                />

                <input
                  type="password"
                  placeholder="Mot de passe"
                  value={editingUser.password}
                  onChange={(e)=>setEditingUser({...editingUser,password:e.target.value})}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                />

                <input
                  type="text"
                  placeholder="Numero de téléphone"
                  value={editingUser?.phone || ""}
                  onChange={(e)=>setEditingUser({...editingUser,phone :e.target.value})}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                />

                <div className="flex items-center justify-end gap-3 pt-2">
                  <button
                    onClick={()=> setEditingUser(null)}
                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
                  >
                    Annuler
                  </button>
                  <button
                    onClick={updateUser}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
                  >
                    Enregister
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* MODAL ADD PERMISSION TO ROLE */}
        {selectedRoleIdForPerm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
            <div className="w-full max-w-2xl rounded-xl bg-white shadow-xl border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-800">
                  Ajouter une permission au role ID : {selectedRoleIdForPerm}
                </h3>
                <button
                  onClick={()=> {
                    setSelectedPermissionId("");
                    setSelectedRoleIdForPerm(null);
                  }}
                  className="text-gray-500 hover:text-gray-700"
                >
                  ✕
                </button>
              </div>

              <div className="p-6 space-y-4">
                <select
                  value={selectedPermissionId}
                  onChange={(e)=>setSelectedPermissionId(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-orange-400"
                >
                  <option value="">Choisir une permission</option>
                  {[...permissions]
                    .sort((a, b) => a.name.localeCompare(b.name))
                    .map((permission) => (
                      <option
                        key={permission.idPermission}
                        value={permission.idPermission}
                      >
                        {permission.name}
                      </option>
                  ))}
                </select>

                <div className="flex items-center justify-end gap-3">
                  <button
                    onClick={()=> {
                      setSelectedPermissionId("");
                      setSelectedRoleIdForPerm(null);
                    }}
                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
                  >
                    Annuler
                  </button>
                  <button
                    onClick={affectPermissionToRole}
                    className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg"
                  >
                    Ajouter
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* MODAL ROLE PICKER */}
        {selectedUser && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
            <div className="w-full max-w-xl rounded-xl bg-white shadow-xl border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-800">Choisir un rôle</h3>
                <button
                  onClick={()=> setSelectedUser(null)}
                  className="text-gray-500 hover:text-gray-700"
                >
                  ✕
                </button>
              </div>

              <div className="p-6 space-y-4">
                <select
                  defaultValue=""
                  onChange={(e) => {
                    const roleId = e.target.value;
                    const userHasRoles = users.find(u=>u.idUser === selectedUser)?.userRoles?.length>0
                    if(userHasRoles){
                        updateRoleUser(selectedUser,roleId);
                    }else{
                        assignRoleToUser(roleId);
                    }
                  }}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                >
                  <option value="">-- Sélectionner --</option>
                  {roles
                    .filter(role => role.name !== "CLIENT")
                    .map((role) => (
                      <option key={role.idRole} value={role.idRole}>
                        {role.name}
                      </option>
                  ))}
                </select>

                <div className="flex justify-end">
                  <button
                    onClick={()=> setSelectedUser(null)}
                    className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
                  >
                    Annuler
                  </button>
                </div>
              </div>

            </div>
          </div>
        )}

        {/* MODAL CREATE USER */}
        {showCreateUser && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
            <div className="w-full max-w-xl rounded-xl bg-white shadow-xl border border-gray-200">
              <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-800">Créer utilisateur</h3>
                <button
                  onClick={()=> setShowCreateUser(false)}
                  className="text-gray-500 hover:text-gray-700"
                >
                  ✕
                </button>
              </div>

              <div className="p-6">
                <form onSubmit={handleCreateUser} className="space-y-4">

                  <input
                    type="text"
                    name="fullName"
                    placeholder="Nom complet"
                    value={userForm.fullName}
                    onChange={handleChange}
                    required
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                  />

                  <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={userForm.email}
                    onChange={handleChange}
                    required
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                  />

                  <input
                    type="password"
                    name="password"
                    placeholder="Mot de passe"
                    value={userForm.password}
                    onChange={handleChange}
                    required
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                  />

                  <input
                    name="phone"
                    placeholder="Téléphone"
                    value={userForm.phone}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
                  />

                  <div className="flex gap-3 justify-end pt-2">
                    <button
                      type="button"
                      onClick={() => setShowCreateUser(false)}
                      className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
                    >
                      Annuler
                    </button>

                    <button
                      type="submit"
                      className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
                    >
                      Créer utilisateur
                    </button>
                  </div>

                </form>
              </div>

            </div>
          </div>
        )}
        {/*Create a team */}
      {showTeamForm && (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
    <div className="w-full max-w-xl rounded-xl bg-white shadow-xl border border-gray-200">
      <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h3 className="text-lg font-semibold text-gray-800">
          Créer une Team
        </h3>
        <button
          onClick={() => setShowTeamForm(false)}
          className="text-gray-500 hover:text-gray-700"
        >
          ✕
        </button>
      </div>

      <form onSubmit={handleCreateTeam} className="p-6 space-y-4">
        <input
          type="text"
          placeholder="Nom"
          value={teamForm.name}
          onChange={(e) =>
            setTeamForm({ ...teamForm, name: e.target.value })
          }
          required
          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />

        <input
          type="text"
          placeholder="Description"
          value={teamForm.description}
          onChange={(e) =>
            setTeamForm({ ...teamForm, description: e.target.value })
          }
          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />

        <div className="flex justify-end gap-3">
          <button
            type="button"
            onClick={() => setShowTeamForm(false)}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
          >
            Annuler
          </button>

          <button
            type="submit"
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
          >
            Créer
          </button>
        </div>
      </form>
    </div>
  </div>
)}
    {teamEditing && (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
    <div className="w-full max-w-xl rounded-xl bg-white shadow-xl border border-gray-200">
      <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
        <h3 className="text-lg font-semibold text-gray-800">
          Modifier Team
        </h3>
        <button
          onClick={() => setTeamEditing(null)}
          className="text-gray-500 hover:text-gray-700"
        >
          ✕
        </button>
      </div>

      <div className="p-6 space-y-4">
        <input
          type="text"
          value={teamEditing.name}
          onChange={(e) =>
            setTeamEditing({ ...teamEditing, name: e.target.value })
          }
          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />

        <input
          type="text"
          value={teamEditing.description}
          onChange={(e) =>
            setTeamEditing({
              ...teamEditing,
              description: e.target.value,
            })
          }
          className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
        />

        <div className="flex justify-end gap-3">
          <button
            onClick={() => setTeamEditing(null)}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
          >
            Annuler
          </button>

          <button
            onClick={updateTeam}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg"
          >
            Enregistrer
          </button>
        </div>
      </div>
    </div>
  </div>
)}
    {/* MESSAGE */}
        {message && (
            <div className="rounded-2xl border border-blue-200 bg-gradient-to-r from-blue-50 to-indigo-50 px-5 py-4 shadow-sm">
              <div className="flex items-start gap-3">
                <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-blue-600 to-indigo-600 text-white grid place-items-center font-bold">
                  i
                </div>
                <div className="text-sm text-slate-800">
                  <div className="font-bold text-slate-900">Info</div>
                  <div className="mt-0.5">{message}</div>
                </div>
              </div>
            </div>
          )}

  </div>
  </div>
  );
}