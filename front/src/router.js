import { createBrowserRouter, RouterProvider } from "react-router-dom";
import Home1 from "./Home1";
import A1 from "./A1";
import C1 from "./C1";
import ChatPage from "./ChatPage";

const Router = () => {
  const routes = [
    {
      path: "/a",
      element: <A1 />,
    },
    {
      path: "/c",
      element: <C1 />,
    },
    {
      path: "/",
      element: <ChatPage />,
    },
  ];

  const router = createBrowserRouter([...routes]);
  return <RouterProvider router={router} />;
};

export default Router;
