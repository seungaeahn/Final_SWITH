import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Header from "./Header";
import MainPage from "./MainPage";
import NewBoard from "./NewBoard";
import Logout from "./Logout";
import Login from "./Login";
import RegisterUser from "./RegisterUser";
import StudyDetail from "./StudyDetail";
import MyPage from "./MyPage";
import StudyRoom from "../main/StudyRoom/Room";
import ModifyPage from "./ModifyPage";
import FindUser from "./FindUser";
import Admin from "./Admin";
import StudyDetailUpdate from "./StudyDetailUpdate";

// function AuthenticatedRoute({ children }) {
//   const authContext = useAuth();

//   if (authContext.isAuthenticated) return children;

//   return <Navigate to="/" />;
// }

export default function Control() {
  return (
    <div className="Main">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/new" element={<NewBoard />} />
          <Route path="/login" element={<Login />} />
          <Route path="/logout" element={<Logout />} />
          <Route path="/register" element={<RegisterUser />} />
          <Route path="/post_detail/:post_no" element={<StudyDetail />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/study_room/:post_no" element={<StudyRoom />} />
          <Route path="/modify" element={<ModifyPage />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="find" element={<FindUser />} />
          <Route
            path="/StudyDetailUpdate/:post_no"
            element={<StudyDetailUpdate />}
          />
        </Routes>
      </BrowserRouter>
    </div>
  );
}
