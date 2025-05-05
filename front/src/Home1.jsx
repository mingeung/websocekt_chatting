import { useNavigate } from "react-router-dom";

export default function Home1() {
  const navigate = useNavigate();

  return (
    <div>
      <button onClick={() => navigate("/a")}>Go to A</button>
      <button onClick={() => navigate("/c")}>Go to C</button>
    </div>
  );
}
