import axiosClient from "@/lib/axiosClient";

export async function getGames() {
  const response = await axiosClient.get("/games");
  return response.data;
}
