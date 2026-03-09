import ReclamationDetail from "@/app/dashboard/views/ReclamationDetail";

export default async function Page({ params }) {
  const {id} = await params;
  return <ReclamationDetail id={id} />;
}