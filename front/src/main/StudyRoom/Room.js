import Header from "../Header";
import usersUserinfoAxios from "../../token/tokenAxios";

import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import NoticeModal from "./NoticeModal";
import MomentModal from "./MomentModal";
import Notice from "./Notice";
import Moment from "./Moment";
import DayCount from "./DayCount";

import ReactCalendar from "./ReactCalendar";
import TodoApp from "./Todo/TodoApp";
import StudyRoomTitle from "./StudyRoomTitle";
import Application from "./Application";
import ChattingPage from "./Chatting/pages/ChattingPage";
import Footer from "../../main/Footer";
import Calender from "./TodoList/Calender";

const StudyRoom = () => {
  const [userData, setUserData] = useState("");

  const [notice, setNotice] = useState({
    post_no: "",
    user_no: "",
    notice_title: "",
    notice_content: "",
    notice_password: "",
  });
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        // 서버에 사용자 정보를 가져오는 요청
        const response = await usersUserinfoAxios.get("/users/userinfo");
        setUserData(response.data);
        console.log(userData);
      } catch (error) {
        console.error("Failed to fetch user data.", error);
      }
    };

    fetchUserData();
  }, []);

  const { post_no } = useParams(); // 동적 라우트 매개변수 가져오기

  return (
    <div>
      <Header />
      <div>
        {/*post_no, user_no */}

        <StudyRoomTitle />
        <Application />
        <div>
          <DayCount />
          <br />
          <br />
          <br />
          <br />
          <br />
          <br />

          <Calender />
          <br />
          <div></div>

          <br />
          <br />
          <br />
          <br />

          <NoticeModal />
          <br />

          <div style={{ display: "flex" }}>
            <Notice />
            <ChattingPage />
          </div>
          <br />
          <br />
          <br />
          <br />
          <br />
          <br />
          <br />
          <br />
          <br />
          <br />
          <MomentModal />
          <Moment />
        </div>
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
        <br />
      </div>
      <Footer />
    </div>
  );
};

export default StudyRoom;
