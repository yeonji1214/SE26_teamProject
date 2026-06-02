import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProject, getProjects } from "../api/projectApi";
import { getIssues } from "../api/issueApi";
import ProjectForm from "../components/ProjectForm";
import ProjectTable, { type ProjectRow } from "../components/ProjectTable";
import type { Project } from "../types/project";

function ProjectPage() {
  const navigate = useNavigate();

  const [projects, setProjects] = useState<Project[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const [projectData, issueData] = await Promise.all([
        getProjects(),
        getIssues(),
      ]);

      const projectsWithIssueCount = projectData.map((project) => {
        const issueCount = issueData.filter((issue) => {
          const issueWithProject = issue as typeof issue & {
            projectId?: number;
            project?: {
              id: number;
            };
          };

          return (
            issueWithProject.projectId === project.id ||
            issueWithProject.project?.id === project.id
          );
        }).length;

        return {
          ...project,
          issueCount,
        };
      });

      setProjects(projectsWithIssueCount);
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "프로젝트 목록을 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const projectRows: ProjectRow[] = useMemo(() => {
    return projects.map((project) => ({
      id: project.id,
      name: project.name,
      description: project.description,
      createdAt: project.createdAt ?? "-",
      issueCount: project.issueCount ?? 0,
    }));
  }, [projects]);

  const handleCreateProject = async (request: {
    name: string;
    description: string;
  }) => {
    try {
      const createdProject = await createProject(request);

      setProjects((prevProjects) => [
        {
          ...createdProject,
          issueCount: 0,
        },
        ...prevProjects,
      ]);
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : "프로젝트 생성 중 오류가 발생했습니다."
      );
    }
  };

  const handleEnterProject = (projectId: number) => {
    navigate(`/dashboard/${projectId}`);
  };

  return (
    <section className="project-page">
      <div className="project-page-header">
        <div>
          <h2>프로젝트</h2>
          <p>백엔드 API에서 프로젝트 목록을 불러옵니다.</p>
        </div>
      </div>

      {isLoading && <p>프로젝트 목록을 불러오는 중입니다.</p>}
      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {!isLoading && !errorMessage && (
        <ProjectTable
          projects={projectRows}
          onEnterProject={handleEnterProject}
        />
      )}

      <ProjectForm onSubmit={handleCreateProject} />
    </section>
  );
}

export default ProjectPage;