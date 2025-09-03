import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/auth.css";
import "../styles/landing.css";
import { Link } from "react-router-dom";



const ResetPassword = () => {
    const navigate = useNavigate();

    const [resetPassword, setResetPassword] = useState({
        email: "",
        otp: "",
        newPassword: "",
    });

    const handleChange = (e) => {
        setResetPassword({ ...resetPassword, [e.target.name]: e.target.value });
    };

    const handleResetPassword = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post("http://localhost:9090/api/verify-otp", resetPassword);
            toast.success("Password reset successful!");
            navigate("/login");
        } catch (err) {
            console.error("Password reset failed:", err);
            toast.error("Password reset failed: " + (err.response?.data || err.message));
        }
    };

    return (
        <div className="pp-auth-wrap">
            <div className="pp-card pp-auth-card position-relative">
                <div className="pp-auth-logo">Pay<span>Pilot</span></div>
                <h2 className="pp-auth-title">Reset Password</h2>


                <form onSubmit={handleResetPassword} className="pp-form-grid">
                    <div className="pp-field">
                        <label htmlFor="otp">OTP</label>
                        <input
                            id="otp"
                            type="text"
                            name="otp"
                            className="pp-input"
                            value={resetPassword.otp}
                            onChange={handleChange}
                            placeholder="Enter OTP"
                            required
                        />
                    </div>
                    <div className="pp-field">
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            type="email"
                            name="email"
                            className="pp-input"
                            value={resetPassword.email}
                            onChange={handleChange}
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div className="pp-field">
                        <label htmlFor="password">New Password</label>
                        <input
                            id="newPassword"
                            type="password"
                            name="newPassword"
                            className="pp-input"
                            value={resetPassword.newPassword}
                            onChange={handleChange}
                            placeholder=""
                            required
                        />
                    </div>

                    <div className="text-end" style={{ marginTop: "6px" }}>
                        <Link to="/send-otp" className="pp-link">Resend OTP</Link>
                    </div>

                    <button type="submit" className="pp-btn pp-btn--primary pp-btn--block">
                        Reset Password
                    </button>
                </form>


                <div className="text-center mt-3">
                    <Link to="/login" className="pp-btn pp-btn--ghost pp-btn--sm">← Back to Home</Link>
                </div>
            </div>
        </div>

    );
};

export default ResetPassword;
