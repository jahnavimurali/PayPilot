import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/auth.css";
import "../styles/landing.css";
import { Link } from "react-router-dom";

const isPasswordValid = (password) =>
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(password);

const ResetPassword = () => {
    const navigate = useNavigate();
    const [confirmPassword, setConfirmPassword] = useState("");

    const [resetPassword, setResetPassword] = useState({
        email: "",
        otp: "",
        newPassword: "",
    });

    const passwordMismatch =
        confirmPassword.length === 0 ? null : confirmPassword === resetPassword.newPassword;

    const handleChange = (e) => {
        setResetPassword({ ...resetPassword, [e.target.name]: e.target.value });
    };

    const handleConfirmChange = (e) => setConfirmPassword(e.target.value);

    const handleResetPassword = async (e) => {
        e.preventDefault();
        if (!isPasswordValid(resetPassword.newPassword)) {
            toast.error(
                "Password must be 8+ chars and include uppercase, lowercase, number, and special character."
            );
            return;
        }
        try {
            const res = await axios.post("http://localhost:9090/api/verify-otp", resetPassword);
            toast.success("Password reset successful!");
            navigate("/login");
        } catch (err) {
            console.error("Password reset failed:", err);
            toast.error("Password reset failed: " + (err.response?.data || err.message));
        }
    };

    const pwHintVisible = resetPassword.newPassword.length > 0 && !isPasswordValid(resetPassword.newPassword);

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
                            placeholder="Create a strong password"
                            required
                        />
                        {pwHintVisible && (
                            <div className="text-danger small mt-1">
                                Must include 8+ chars, uppercase, lowercase, number & special char.
                            </div>
                        )}
                    </div>

                    <div className="pp-field">
                        <label htmlFor="cpassword">Confirm Password</label>
                        <input
                            id="cpassword"
                            className="pp-input"
                            type="password"
                            name="cpassword"
                            value={confirmPassword}
                            onChange={handleConfirmChange}
                            placeholder="Confirm Password"
                            required
                            autoComplete="new-password"
                        />
                        <div className="passwordWarning small mt-1">
                            {passwordMismatch === null
                                ? ""
                                : passwordMismatch
                                    ? "✅ Passwords match"
                                    : "❌ Passwords do not match"}
                        </div>
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
