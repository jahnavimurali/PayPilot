import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Signup from "./components/Signup";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import LandingPage from "./components/LandingPage";

import AddBill from "./components/AddBill";
import ReminderLogViewer from "./components/ReminderLogViewer";
import SendOTP from "./components/SendOTP";
import ResetPassword from "./components/ResetPassword";

import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import './styles/paypilot-bg.css'

function App() {
    return (
        <Router>
            <Routes>
                {/* Default route → Login */}
                <Route path="/" element={<LandingPage />} />

                {/* Auth Routes */}
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/send-otp" element={<SendOTP />} />
                <Route path="/reset-password" element={<ResetPassword />} />
                {/* Protected Routes */}
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/add-bill" element={<AddBill />} />
                <Route path="/reminder-logs" element={<ReminderLogViewer />} />

            </Routes>

            {/* Global Toast Container */}
            <ToastContainer position="top-center" autoClose={3000} />
        </Router>
    );
}

export default App;
