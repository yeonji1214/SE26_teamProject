import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ProjectForm from "../components/ProjectForm";
import ProjectTable, { type ProjectRow } from "../components/ProjectTable";

const initialProjects: ProjectRow[] = [
  {
    id: 1,
    name: "project1",
    description: "이슈 관리 시스템 데모 프로젝트",
    createdAt: "2026-05-22",
    issueCount: 3,
  },
];

function ProjectPage() {
  const navigate = useNavigate();
  const [projects, setProjects] = useState<ProjectRow[]>(initialProjects);

  const handleCreateProject = (request: {
    name: string;
    description: string;
  }) => {
    const newProject: ProjectRow = {
      id: Date.now(),
      name: request.name,
      description: request.description,
      createdAt: new Date().toISOString().slice(0, 10),
      issueCount: 0,
    };

    setProjects((prevProjects) => [newProject, ...prevProjects]);
  };

  const handleEnterProject = () => {
    navigate("/dashboard");
  };

  return (
    <section className="project-page">
      <div className="project-page-header">
        <h2>프로젝트</h2>

        <button type="button" className="primary-button">
          + 프로젝트 추가
        </button>
      </div>

      <ProjectTable projects={projects} onEnterProject={handleEnterProject} />

      <ProjectForm onSubmit={handleCreateProject} />
    </section>
  );
}

export default ProjectPage;