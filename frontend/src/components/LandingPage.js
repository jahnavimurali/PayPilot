import React from "react";
import { Link, Navigate } from "react-router-dom";
import "../styles/landing.css";

export default function LandingPage() {
    const user = localStorage.getItem("user");
    if (user) return <Navigate to="/dashboard" replace />;

    return (
        <div className="pp-hero">

            <div className="container">
                <header className="d-flex justify-content-between align-items-center py-4">

                    <div className="pp-logo">
                        <span className="pp-pay">Pay<span className="pp-pilot">Pilot</span></span>
                    </div>

                    <div className="d-none d-md-flex gap-3">
                        <Link to="/login" className="pp-link">Log In</Link>
                        <Link to="/signup" className="pp-link">Sign Up</Link>
                    </div>
                </header>

                <main className="row justify-content-center">
                    <section className="col-xl-8">
                        <div className="pp-card text-center p-5 p-md-6">
                            <h1 className="display-5 fw-bold mb-2">
                                Stay on top of every bill.
                            </h1>
                            <p className="text-muted lead mb-4">
                                Track, filter, schedule, and get smart reminders — all in one place.
                            </p>

                            <div className="d-flex justify-content-center gap-3 flex-wrap mt-3">
                                <Link to="/login"  className="pp-btn pp-btn--primary pp-btn--lg">Log In</Link>
                                <Link to="/signup" className="pp-btn pp-btn--outline pp-btn--lg">Create Account</Link>
                            </div>

                            <div className="row g-3 text-start mt-5">
                                <div className="col-md-4">
                                    <div className="pp-feature">
                                        <div className="pp-dot" />
                                        <h6 className="mb-1">Smart Reminders</h6>
                                        <small className="text-muted">Never miss due dates with SMS + scheduler.</small>
                                    </div>
                                </div>
                                <div className="col-md-4">
                                    <div className="pp-feature">
                                        <div className="pp-dot" />
                                        <h6 className="mb-1">Payments & History</h6>
                                        <small className="text-muted">Log payments and view clear timelines.</small>
                                    </div>
                                </div>
                                <div className="col-md-4">
                                    <div className="pp-feature">
                                        <div className="pp-dot" />
                                        <h6 className="mb-1">Visual Insights</h6>
                                        <small className="text-muted">Filter bills, analyze spend with charts.</small>
                                    </div>
                                </div>
                            </div>

                            <div className="pp-footnote mt-4">
                                © {new Date().getFullYear()} PayPilot
                            </div>
                        </div>
                    </section>
                </main>
            </div>
        </div>
    );
}
