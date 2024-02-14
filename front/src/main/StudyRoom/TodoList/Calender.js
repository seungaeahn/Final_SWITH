import React, { useState, useEffect } from "react";
import Calendar from "react-calendar"; //npm i react-calendar,
import "react-calendar/dist/Calendar.css"; // css import
import moment from "moment"; // npm i moment
import { useParams } from "react-router-dom";
import axios from "axios";
function ReactCalendar() {
  const { post_no } = useParams();
  const [value, onChange] = useState(new Date()); //내가 선택하는 날짜
  const [mark, setMark] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null); // 선택된 날짜 상태 추가
  const [todo, setTodo] = useState([]); // TodoList를 가져올 데이터, 배열로 설정해준다.

  useEffect(() => {
    // 선택된 날짜가 변경될 때 TodoList 데이터를 불러옴
    if (selectedDate) {
      // Todo 데이터를 불러오는 로직 (axios 등을 사용)
      fetchTodoList(selectedDate);
      console.log(selectedDate);
    }
  }, [selectedDate]);

  //insert
  //const

  //select
  const fetchTodoList = async (todo_date) => {
    try {
      // TODO: 서버에서 해당 날짜의 TodoList를 가져오는 API 호출
      const response = await axios.get(
        `http://localhost:8080/studyRoom/get/Todo/${post_no}/${todo_date}`
      );

      // 가져온 데이터를 TodoList 상태에 업데이트
      setTodo(response.data); //값을 넣어줌
      console.log(response.data);
    } catch (error) {
      console.error("TodoList를 불러오는데 실패했습니다.", error);
    }
  };

  return (
    <div>
      <Calendar
        onChange={(todo_date) => {
          onChange(todo_date);
          setSelectedDate(moment(todo_date).format("YYYY-MM-DD"));
        }}
        formatDay={(locale, todo_date) => moment(todo_date).format("DD")}
        value={value}
        className="mx-auto w-full text-sm border-b"
      />{" "}
      <h2>{moment(value).format("YYYY년 MM월 DD일")} 할 일</h2>
      {/* TodoList 컴포넌트를 렌더링하고 선택된 날짜에 해당하는 할 일 목록을 전달 */}
      <div>
        <table>
          <thead>
            <tr>
              <th>할일 목록</th>
            </tr>
          </thead>
          <tbody>
            {todo.length > 0 ? (
              todo.map((todo) => (
                <tr key={todo.value}>
                  <td>{todo.todo_list}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td>No Todo List</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
export default ReactCalendar;

/*
<ul>
          {Array.isArray(todo.todo_list) && todo.todo_list.length > 0 ? (
            todo.todo_list.map((item) => (
              <li key={item.todo_date}>
                {/*date를 기준으로 리스트를 가져옵니다. *
                <div className="text-gray-500 mt-4">
                  <p>할 일 {item.todo_list}</p>
                </div>
              </li>
            ))
          ) : (
            <p>No Todo List.</p>
          )}
        </ul>
 */
