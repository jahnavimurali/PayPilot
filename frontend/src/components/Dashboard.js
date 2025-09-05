import React, { useState } from "react";
import "react-toastify/dist/ReactToastify.css";
import AddBill from "./AddBill";
import ViewBills from "./ViewBills";
import FilterBills from "./FilterBills";
import PaymentManager from "./PaymentManager";
import ScheduledPaymentManager from "./ScheduledPaymentManager";
import ReminderSettings from "./ReminderSettings";
import NotificationPopup from "./NotificationPopup";


const Dashboard = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const [activeView, setActiveView] = useState("add");

    const handleLogout = () => {
        localStorage.removeItem("user");
        window.location.href = "/login";
    };

    if (!user) {
        return (
            <div className="container mt-5">
                <h2>Access Denied 🚫</h2>
                <p>Please login first.</p>
            </div>
        );
    }

    return (
        <>
            <nav className="navbar navbar-expand-lg navbar-light bg-light">
                <div className="container-fluid">
                    <a
                        className="navbar-brand"
                        href="#"
                        style={{
                            color: '#ff4081',
                            fontWeight: 'bold',
                            fontSize: '1.75rem',
                            textShadow: '2px 2px #ffeb3b'
                        }}
                    >
                        PayPilot
                    </a>
                    <button
                        className="navbar-toggler"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarNav"
                        aria-controls="navbarNav"
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                    >
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarNav">
                        <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                            <li className="nav-item">
                                <button
                                    className={`nav-link btn btn-link ${activeView === "add" ? "active" : ""}`}
                                    onClick={() => setActiveView("add")}
                                >
                                    ➕ Add Bill
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={`nav-link btn btn-link ${activeView === "view" ? "active" : ""}`}
                                    onClick={() => setActiveView("view")}
                                >
                                    📄 View Bills
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={`nav-link btn btn-link ${activeView === "filter" ? "active" : ""}`}
                                    onClick={() => setActiveView("filter")}
                                >
                                    🔍 Filter Bills
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={`nav-link btn btn-link ${activeView === "payments" ? "active" : ""}`}
                                    onClick={() => setActiveView("payments")}
                                >
                                    💳 Payments
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={`nav-link btn btn-link ${activeView === "scheduled" ? "active" : ""}`}
                                    onClick={() => setActiveView("scheduled")}
                                >
                                    📅 Scheduled
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={`nav-link btn btn-link ${activeView === "reminders" ? "active" : ""}`}
                                    onClick={() => setActiveView("reminders")}
                                >
                                    ⏰ Reminders
                                </button>
                            </li>
                        </ul>
                        <div className="d-flex align-items-center">
                            <span className="me-3">Welcome, {user.name}</span>
                            <button
                                className="btn btn-outline-danger"
                                onClick={handleLogout}
                            >
                                Logout
                            </button>
                        </div>
                    </div>
                </div>
            </nav>
            <NotificationPopup userId={user.id} />
            <div className="container mt-4">
                {activeView === "add" && <AddBill />}
                {activeView === "view" && <ViewBills />}
                {activeView === "filter" && <FilterBills />}
                {activeView === "payments" && <PaymentManager />}
                {activeView === "scheduled" && <ScheduledPaymentManager />}
                {activeView === "reminders" && <ReminderSettings />}
            </div>
        </>
    );
};

export default Dashboard;
