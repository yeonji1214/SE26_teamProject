export interface ProjectRow {
  id: number;
  name: string;
  description: string;
  createdAt: string;
  issueCount: number;
}

interface ProjectTableProps {
  projects: ProjectRow[];
  onEnterProject: (projectId: number) => void;
}

function ProjectTable({ projects, onEnterProject }: ProjectTableProps) {
  return (
    <div className="project-table-wrapper">
      <table className="project-table">
        <thead>
          <tr>
            <th>프로젝트명</th>
            <th>설명</th>
            <th>생성일</th>
            <th>이슈 수</th>
            <th>액션</th>
          </tr>
        </thead>

        <tbody>
          {projects.length > 0 ? (
            projects.map((project) => (
              <tr key={project.id}>
                <td>{project.name}</td>
                <td>{project.description || "-"}</td>
                <td>{project.createdAt}</td>
                <td>{project.issueCount}</td>
                <td>
                  <button
                    type="button"
                    className="table-action-button"
                    onClick={() => onEnterProject(project.id)}
                  >
                    입장
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td className="empty-table-message" colSpan={5}>
                등록된 프로젝트가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default ProjectTable;